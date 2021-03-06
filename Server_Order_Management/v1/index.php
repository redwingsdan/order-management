<?php

require_once '../include/DbHandler.php';
require_once '../include/PassHash.php';
require '.././libs/Slim/Slim.php';

include '.././composer/vendor/autoload.php';

\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();

// User id from db - Global Variable
$user_id = NULL;

/**
 * Adding Middle Layer to authenticate every request
 * Checking if the request has valid api key in the 'Authorization' header
 */
function authenticate(\Slim\Route $route) {

	// Getting request headers
    $headers = apache_request_headers();
    $response = array();
    $app = \Slim\Slim::getInstance();

    // Verifying Authorization Header
    if (isset($headers['Authorization'])) {
        $db = new DbHandler();

        // get the api key
		global $api_key2;
        $api_key = $headers['Authorization'];
        // validating api key
        if (!$db->isValidApiKey($api_key)) {
            // api key is not present in users table
            $response["error"] = true;
            $response["message"] = "Access Denied. Invalid Api key";
            echoResponse(401, $response);
            $app->stop();
        } else {
            global $user_id;
            // get user primary key id
            $user_id = $db->getUserId($api_key);
        }
    } else {
        // api key is missing in header
        $response["error"] = true;
        $response["message"] = "Api key is missing";
		$response["message2"] = $_SERVER['SERVER_ADDR'];
        echoResponse(400, $response);
        $app->stop();
    }
}

/**
 * ----------- METHODS WITHOUT AUTHENTICATION ---------------------------------
 */
/**
 * User Registration
 * url - /register
 * method - POST
 * params - name, email, password
 */
$app->post('/register', function() use ($app) {
            // check for required params
			global $user_id;
			$request = $app->request();
			$body = $request->getBody();
			$input = json_decode($body);
			
			$username = (string)$input->username;
            $password = (string)$input->password;
			$email = (string)$input->email;
			
            $response = array();

            // validating email address
            validateEmail($email);

			$noflag = false;
            $db = new DbHandler();
			$answer = $db->checkUpdate($user_id);	
			if($answer != null){
				$response["error"] = array();
				$response["error"] = $answer;
                $response["message"] = "Out of sync with database. Please refresh";
				echoResponse(201, $response);
				$noflag = true;
			}
			if(!$noflag){
				$res = $db->createUser($username, $password, $email);
				if ($res == USER_CREATED_SUCCESSFULLY) {
					$answ = $db->addUpdate($user_id);
					$response["error"] = false;
					$response["message"] = "You are successfully registered";
				} else if ($res == USER_CREATE_FAILED) {
					$response["error"] = true;
					$response["message"] = "Oops! An error occurred while registering";
				} else if ($res == USER_ALREADY_EXISTED) {
					$response["error"] = true;
					$response["message"] = "Sorry, this email or username already exists";
				}
				echoResponse(201, $response);
			}
        });

/**
 * User Login
 * url - /login
 * method - POST
 * params - username, password
 */
$app->post('/login', function() use ($app) {
            // check for required params
			$request = $app->request();
			$body = $request->getBody();
			$input = json_decode($body);
            // reading post params
            $username = (string)$input->username;
            $password = (string)$input->password;
			
            $response = array();

            $db = new DbHandler();
            // check for correct username and password
            if ($db->checkLogin($username, $password)) {
                // get the user by username
                $user = $db->getUserByUsername($username);

                if ($user != NULL) {
                    $response["error"] = false;
					$response['message'] = "Logged in Successfully!";
                    $response['username'] = $user['username'];
                    $response['email'] = $user['email'];
                    $response['apiKey'] = $user['api_key'];
                    $response['createdAt'] = $user['created_at'];
					$response['id'] = $user['id'];
                } else {
                    // unknown error occurred
                    $response['error'] = true;
                    $response['message'] = "An error occurred. Please try again";
                }
            } else {
                // user credentials are wrong
                $response['error'] = true;
                $response['message'] = 'Login failed. Incorrect credentials';
            }
            echoResponse(200, $response);
        });

/*
 * ------------------------ METHODS WITH AUTHENTICATION ------------------------
 */
		 
/**
 * Listing all orders
 * method GET
 * url /orders         
 */
$app->get('/orders', 'authenticate', function() {	
			global $user_id;
            $response = array();
            $db = new DbHandler();
			$db->deleteUpdate($user_id);
            $result = $db->getAllOrders();
            $response["error"] = false;
            $response["orders"] = array();
			
            while ($order = $result->fetch_assoc()) {
				$numvar = "";
                $tmp = array();
                $tmp["id"] = $order["id"];
                $tmp["order"] = $order["order"];
				$tmp["customer_name"] = $order["customer_name"];
				$tmp["build_type"] = $order["build_type"];
				$tmp["pdf_path"] = $order["pdf_path"];
                $tmp["status"] = $order["status"];
                $tmp["createdAt"] = $order["created_at"];
				$tmp["assigned_to"] = $order["assigned_to"];
				$result2 = $db->getAllOrders2($order["id"]);
					while ($part = $result2->fetch_assoc()) {
						$numvar = $numvar . "|" . $part["count"];
					}
				$tmp["num_builds"] = $numvar;
                array_push($response["orders"], $tmp);
            }
            echoResponse(200, $response);
        });

/**
 * Listing single order of particular user
 * method GET
 * url /orders/:id
 * Will return 404 if the order doesn't belongs to user
 */
$app->get('/orders/:id', 'authenticate', function($order_id) {
            global $user_id;
            $response = array();
            $db = new DbHandler();
			$db->deleteUpdate($user_id);	
			$result = $db->getAllOrders();
            $response["error"] = false;
            $response["orders"] = array();

            while ($order = $result->fetch_assoc()) {
				$numvar = "";
                $tmp = array();
                $tmp["id"] = $order["id"];
                $tmp["order"] = $order["order"];
				$tmp["customer_name"] = $order["customer_name"];
				$tmp["build_type"] = $order["build_type"];
				$tmp["pdf_path"] = $order["pdf_path"];
                $tmp["status"] = $order["status"];
                $tmp["createdAt"] = $order["created_at"];
				$tmp["assigned_to"] = $order["assigned_to"];
				$result2 = $db->getAllOrders2($order["id"]);
					while ($part = $result2->fetch_assoc()) {
						$numvar = $numvar . "|" . $part["count"];
					}
				$tmp["num_builds"] = $numvar;
				$user_test = $db->getUserByUsername($order["assigned_to"]);
				$id_test = $user_test["id"];
					if($user_id == $id_test){
						array_push($response["orders"], $tmp);
					}
            }
            echoResponse(200, $response);
        });

/**
 * Creating new order in db
 * method POST
 * params - name
 * url - /orders
 */
$app->post('/orders', 'authenticate', function() use ($app) {
			global $user_id;
            verifyRequiredParams(array('order'));
			$request = $app->request();
			$body = $request->getBody();
			$input = json_decode($body,true);

			$newCount;
			$r2 = "";
			for($i = 0, $size = count($input); $i < $size; ++$i){
				$teststring = "buildType" . $i;
				if(array_key_exists($teststring, $input)){
					$r2 = $r2 . "|";
					$newCount[$i] = $input[$teststring];
					$r2 = $r2 . $input[$teststring];
				}
			}
			$newBuilds;
			$r7 = "";
			for($i = 0, $size = count($input); $i < $size; ++$i){
				$teststring = "num_builds" . $i;
				if(array_key_exists($teststring, $input)){
					$r7 = $r7 . "|";
					$newBuilds[$i] = $input[$teststring];
					$r7 = $r7 . $input[$teststring];
				}
			}
			$flag = false;
			$noflag = false;
			$r1 = $input['custName'];
			$r3 = $input['pdfName'];
			$r4 = $input['assignedTo'];
			$r5 = $input['status'];
			$r6 = $input["order_id"];
			global $user_id;
            $db = new DbHandler();			
			$answer = $db->checkUpdate($user_id);		//returns null if nothing is in table
			$response = array();
			if($answer != null){
				$response["error"] = array();
				$response["error"] = $answer;
                $response["message"] = "Out of sync with database. Please refresh";
				echoResponse(201, $response);
				$noflag = true;
			}
			$result = $db->getAllOrders();
            while ($order = $result->fetch_assoc()) {
				if($r6 == $order["order"]){
					$flag = true;
				}
			}
			if(!$flag){
				if(!$noflag){
					$order_id = $db->createOrder($user_id, $r1, $r2, $r3, $r4, $r5, $r6, $r7, $newCount, $newBuilds);
					if ($order_id != NULL) {
						$answ = $db->addUpdate($user_id);
						$response["error"] = false;
						$response["message"] = "Order created successfully";
						$response["order_id"] = $order_id;
						$response["status"] = $r5;
						$response["ordernum"] = $r6;
						$response["num_builds"] = $newBuilds;
						$response["builds"] = $newCount;
						echoResponse(201, $response);
					} else {
						$response["error"] = true;
						$response["message"] = "Failed to create order. Please try again";
						$response["order_id"] = $order_id;
						$response["num_builds"] = $newBuilds;
						echoResponse(200, $response);
					}  	
				}
			}
			else if (!$noflag){
                $response["error"] = true;
                $response["message"] = "Failed to create order. Please try again";
				$response["order_id"] = $order_id;
				$response["next"] = $r7;
				$response["num_builds"] = $newBuilds;
                echoResponse(200, $response);
            }  	
        });
		
/**
 * Listing all parts
 * method GET
 * url /parts        
 */
 $app->get('/parts', 'authenticate', function() {
            global $user_id;
            $response = array();
            $db = new DbHandler();
			
			$result = $db->getAllParts();

            $response["error"] = false;
            $response["orders"] = array();

            while ($part = $result->fetch_assoc()) {
                $tmp = array();
                $tmp["name"] = $part["name"];
				$tmp["chassis"] = $part["chassis"];
				$tmp["motherboard"] = $part["motherboard"];
				$tmp["cpu"] = $part["cpu"];
				$tmp["memory"] = $part["memory"];
				$tmp["video"] = $part["video_card"];
				$tmp["harddrive"] = $part["hard_drive"];
				$tmp["encoder"] = $part["encoder_card"];
				$tmp["decoder"] = $part["decoder_card"];
				$tmp["other"] = $part["other"];
                array_push($response["orders"], $tmp);
            }
            echoResponse(200, $response);
        });

		
/**
 * Creating new part in db
 * method POST
 * params - name
 * url - /parts
 */
$app->post('/parts', 'authenticate', function() use ($app) {
			global $user_id;	
			$request = $app->request();
			$body = $request->getBody();
			$input = json_decode($body,true);

			$flag = false;
			$r1 = $input['name'];
			$r2 = $input['chassis'];
			$r3 = $input['motherboard'];
			$r4 = $input['cpu'];
			$r5 = $input['memory'];
			$r6 = $input['video'];
			$r7 = $input['harddrive'];
			$r8 = $input['encoder'];
			$r9 = $input['decoder'];
			$r10 = $input['other'];
			
			global $user_id;
			$noflag = false;
            $db = new DbHandler();		
			$answer = $db->checkUpdate($user_id);
            $response = array();			
			if($answer != null){
				$response["error"] = array();
				$response["error"] = $answer;
                $response["message"] = "Out of sync with database. Please refresh";
				echoResponse(201, $response);
				$noflag = true;
			}
			$result = $db->getAllParts();
            while ($part = $result->fetch_assoc()) {
                if($r1 == $part["name"]){
					$flag = true;
				}
			}
			if(!$flag){
				if(!$noflag){
					$order_id = $db->createPart($r1, $r2, $r3, $r4, $r5, $r6, $r7, $r8, $r9, $r10);
					if ($order_id == true) {
						$answ = $db->addUpdate($user_id);
						$response["error"] = false;
						$response["message"] = "Order created successfully";
						$response["order_id"] = $order_id;
						$response["name"] = $r1;
						$response["chassis"] = $r2;
						$response["motherboard"] = $r3;
						$response["cpu"] = $r4;
						$response["memory"] = $r5;
						$response["video"] = $r6;
						$response["harddrive"] = $r7;
						$response["encoder"] = $r8;
						$response["decoder"] = $r9;
						$response["other"] = $r10;
						echoResponse(201, $response);
					} else {
						$response["error"] = true;
						$response["message"] = "Failed to create order. Please try again";
						$response["order_id"] = $order_id;
						echoResponse(200, $response);
					} 
				}
			}
			else if(!$noflag){
				$response["error"] = true;
                $response["message"] = "Failed to create order. Please try again";
				$response["order_id"] = $order_id;
                echoResponse(200, $response);
			}			
        });

/**
 * Updating existing order
 * method PUT
 * params order, status
 * url - /orders/:id
 */
$app->put('/orders/:id', 'authenticate', function($order_id) use($app) {
			$request = $app->request();
			$body = $request->getBody();
			$input = json_decode($body,true);
			
			$r1 = $input['custName'];
		    $r2 = $input['buildType'];
			$r3 = $input['pdfName'];
			$r4 = $input['assignedTo'];
			$r5 = $input['status'];
			$r6 = $input['position'];
			$r7 = $input['num_builds'];
			$r8 = $input['order_id'];
			
			$newCount = explode('|', $r2);
			$newBuilds = explode('|', $r7);
            global $user_id;        
			$noflag = false;			
            $db = new DbHandler();
			$answer = $db->checkUpdate($user_id);
            $response = array();			
			if($answer != null){
				$response["error"] = array();
				$response["error"] = $answer;
                $response["message"] = "Out of sync with database. Please refresh";
				echoResponse(201, $response);
				$noflag = true;
			}
			$flag = false;
			$result = false;
			$result2 = $db->getAllOrders();
            while ($order = $result2->fetch_assoc()) {
				if($r8 == $order["order"]){
					if($r6 != $order["id"]){
						$flag = true;
					}
				}
			}
			if(!$flag){
				if(!$noflag){
					$result = $db->updateOrder($user_id, $r6, $r4, $r5, $r7, $r8, $r3, $r2, $r1, $newBuilds, $newCount);
					if ($result) {
						// order updated successfully
						$answ = $db->addUpdate($user_id);
						$response["error"] = false;
						$response["message"] = "Order updated successfully";
						$response["order_id"] = $r6;
						$response["assign"] = $r4;
						$response["status"] = $r5;
						$response["num_builds"] = $r7;
						$response["custName"] = $r1;
						$response["buildType"] = $r2;
						$response["pdfName"] = $r3;
						$response["order_id2"] = $r8;
					} else {
						// order failed to update
						$response["error"] = true;
						$response["message"] = "Order failed to update. Please try again!";
						$response["msg1"] = $user_id;
						$response["msg2"] = $r6;
						$response["msg3"] = $r4;
						$response["msg4"] = $r5;
						$response["num_builds"] = $r7;
						$response["buildType"] = $r2;
						$response["num_builds2"] = $newBuilds[1];
						$response["buildType2"] = $newCount[1];
					}
				}
			}
			else if(!$noflag){
                // order failed to update
                $response["error"] = true;
                $response["message"] = "Order failed to update. Please try again!";
            }
            echoResponse(200, $response);
        });

/**
 * Deleting order. Only the admin can delete orders
 * method DELETE
 * url /orders/id
 */
 $app->delete('/orders/:id', 'authenticate', function($id) use($app) {
			global $user_id;
            $db = new DbHandler();
			$answer = $db->checkUpdate($user_id);	
			$response = array();
			$noflag = false;
			if($answer != null){
				$response["error"] = array();
				$response["error"] = $answer;
                $response["message"] = "Out of sync with database. Please refresh";
				echoResponse(201, $response);
				$noflag = true;
			}
			if(!$noflag){
				$result = $db->deleteOrder($user_id, $id);
				if ($result) {
					// order deleted successfully
					$answ = $db->addUpdate($user_id);
					$response["error"] = false;
					$response["message"] = "Order deleted succesfully";
					$response["order_id"] = $id;
				} else {
					// order failed to delete
					$response["error"] = true;
					$response["message"] = "Order failed to delete. Please try again!";
				}
				echoResponse(200, $response);
			}
        });
		
/**
 * Importing a pdf file to be parsed
 * data is returned and used as customer name 
 * and order number
 */
 $app->post('/import', 'authenticate', function() use ($app){
		$request = $app->request();
		$body = $request->getBody();
		$input = json_decode($body,true);
		$name = $input['name'];
		$string = $name . '.pdf';
		$response["text"] = array();
		$parser = new \Smalot\PdfParser\Parser();
		try{
		$pdf = $parser->parseFile($string);
		$pages  = $pdf->getPages();
		foreach ($pages as $page) {
			$tempvar = $page->getText();
			array_push($response["text"], $tempvar);
		}
		$tmp["error"] = false;
        $tmp["message"] = "Order Imported Successfully!";
		$tmp["customer_name"] = "Danny";
		$tmp["order"] = "54321";
		$response["orders"] = array();
		array_push($response["orders"], $tmp);
		echoResponse(200, $response);
		}
		catch(Exception $e){
		$tmp["error"] = true;
        $tmp["message"] = "Something went wrong";
		echoResponse(200, $response);
		}
	});
	
/**
 * Listing all users
 * method GET
 * url /users         
 */
 $app->get('/users', 'authenticate', function() {
            global $user_id;
            $response = array();
            $db = new DbHandler();
			$result = $db->getAllUsers();
            $response["error"] = false;
            $response["orders"] = array();
            while ($user = $result->fetch_assoc()) {
                $tmp = array();
				$tmp["id"] = $user["id"];
                $tmp["username"] = $user["username"];
                array_push($response["orders"], $tmp);
            }
            echoResponse(200, $response);
      });
	  
/**
 * Deleting part. Only the admin can delete parts
 * method DELETE
 * url /parts
 */
 $app->delete('/parts/:id', 'authenticate', function($id) use($app) {
			global $user_id;
			$db = new DbHandler();
            $answer = $db->checkUpdate($user_id);	
			$response = array();
			$noflag = false;
			if($answer != null){
				$response["error"] = array();
				$response["error"] = $answer;
                $response["message"] = "Out of sync with database. Please refresh";
				echoResponse(201, $response);
				$noflag = true;
			}
			if(!$noflag){
				$result = $db->deletePart($id);
				if ($result) {
					// part deleted successfully
					$answ = $db->addUpdate($user_id);
					$response["error"] = false;
					$response["message"] = "Order deleted succesfully";
					$response["order_id"] = $id;
					$response["statement"] = $result;
				} else {
					// part failed to delete
					$response["error"] = true;
					$response["message"] = "Order failed to delete. Please try again!";
					$response["order_id"] = $id;
				}
				echoResponse(200, $response);
			}
      });
	  
/**
 * Deleting user. Only the admin can delete users
 * method DELETE
 * url /users
 */
	  $app->delete('/users/:id', 'authenticate', function($id) use($app) {
			global $user_id;
            $db = new DbHandler();
            $answer = $db->checkUpdate($user_id);	
			$response = array();
			$noflag = false;
			if($answer != null){
				$response["error"] = array();
				$response["error"] = $answer;
                $response["message"] = "Out of sync with database. Please refresh";
				echoResponse(201, $response);
				$noflag = true;
			}
			if(!$noflag){
				$user = $db->getUserByUsername($id);
				if($user['id'] != $user_id){
					$result = $db->deleteUser($id);
				}
				else{
					$result = false;
				}
				if ($result) {
					// user deleted successfully
					$answ = $db->addUpdate($user_id);
					$db->deleteUpdate($user['id']);
					$response["error"] = false;
					$response["message"] = "Order deleted succesfully";
					$response["order_id"] = $id;
				} else {
					// user failed to delete
					$response["error"] = true;
					$response["message"] = "Order failed to delete. Please try again!";
					$response["order_id"] = $id;
				}
				echoResponse(200, $response);
			}
      });
	  
/**
 * Verifying required params posted or not
 */
function verifyRequiredParams($required_fields) {
    $error = false;
    $error_fields = "";
    $request_params = array();
    $request_params = $_REQUEST;
    // Handling PUT request params
    if ($_SERVER['REQUEST_METHOD'] == 'PUT') {
        $app = \Slim\Slim::getInstance();
        parse_str($app->request()->getBody(), $request_params);
    }
    foreach ($required_fields as $field) {
        if (!isset($request_params[$field]) || strlen(trim($request_params[$field])) <= 0) {
            #$error = true;
            $error_fields .= $field . ', ';
        }
    }

    if ($error) {
        // Required field(s) are missing or empty
        // echo error json and stop the app
        $response = array();
        $app = \Slim\Slim::getInstance();
        $response["error"] = true;
        $response["message"] = 'Required field(s) ' . substr($error_fields, 0, -2) . ' is missing or empty';
        echoResponse(400, $response);
        $app->stop();
    }
}

/**
 * Validating email address
 */
function validateEmail($email) {
    $app = \Slim\Slim::getInstance();
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $response["error"] = true;
        $response["message"] = 'Email address is not valid';
        echoResponse(400, $response);
        $app->stop();
    }
}

/**
 * Echoing json response to client
 * @param String $status_code Http response code
 * @param Int $response Json response
 */
function echoResponse($status_code, $response) {
    $app = \Slim\Slim::getInstance();
    // Http response code
    $app->status($status_code);

    // setting response content type to json
    $app->contentType('application/json');
	
    echo json_encode($response);
}

$app->run();
?>