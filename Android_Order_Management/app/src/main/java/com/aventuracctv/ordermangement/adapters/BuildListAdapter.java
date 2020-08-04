package com.aventuracctv.ordermangement.adapters;

        import android.content.Context;
        import android.content.res.Resources;
        import android.support.v7.widget.CardView;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.TextView;

        import com.aventuracctv.ordermangement.R;
        import com.aventuracctv.ordermangement.data.BuildItem;
        import com.aventuracctv.ordermangement.interfaces.AssignBuildCallback;
        import com.aventuracctv.ordermangement.interfaces.BuildSwapCallback;
        import com.aventuracctv.ordermangement.interfaces.OpenPDFCallback;

        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;

public class BuildListAdapter extends RecyclerView.Adapter<BuildListAdapter.ViewHolder> {
        private List<BuildItem> buildItems;
        private final Context mContext;
        private AssignBuildCallback buildCallback;
        private OpenPDFCallback pdfCallback;
        private BuildSwapCallback buildSwapCallback;

        public BuildListAdapter(Context context, AssignBuildCallback buildCallback, OpenPDFCallback pdfCallback, BuildSwapCallback buildSwapCallback, final List<BuildItem> buildItems) {
            mContext = context;
            this.buildCallback = buildCallback;
            this.pdfCallback = pdfCallback;
            this.buildSwapCallback = buildSwapCallback;
            this.buildItems = buildItems;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public BuildListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.build_item, null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {

            final int pos = position;

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData

            viewHolder.txtCustomerName.setText(buildItems.get(position).getCustomerName());
            viewHolder.txtBuildItem.setText(buildItems.get(position).getBuildType());
            viewHolder.btnPDF.setTag(buildItems.get(position));
            viewHolder.txtAssignedTo.setText(buildItems.get(position).assignedTo());
            viewHolder.btnAssign.setTag(buildItems.get(position));
            if (buildItems.get(position).isAssigned()) {
                viewHolder.btnAssign.setClickable(false);
                viewHolder.btnAssign.setSelected(true);
                if (buildItems.get(position).getProgress() == 2) {
                    viewHolder.btnAssign.setText("In Progress");
                    viewHolder.cv.setCardBackgroundColor(mContext.getResources().getColor(R.color.inProgress));
                } else {
                    viewHolder.btnAssign.setText("Complete");
                    viewHolder.cv.setCardBackgroundColor(mContext.getResources().getColor(R.color.done));
                }
            } else {
                viewHolder.cv.setCardBackgroundColor(mContext.getResources().getColor(R.color.unassigned));
                viewHolder.btnAssign.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Button btn = (Button) v;
                        BuildItem item = (BuildItem) btn.getTag();
                        item.setPosition(pos);
                        buildCallback.assignBuildItem(btn, item);
                    }
                });
            }

            viewHolder.btnPDF.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ImageButton btn = (ImageButton) v;
                    BuildItem item = (BuildItem) btn.getTag();
                    item.setPosition(pos);
                    pdfCallback.openPDF(btn, item);
                }
            });
        }

        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(buildItems, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            buildSwapCallback.buildSwap(fromPosition, toPosition);
            return true;
        }

        // inner class to hold a reference to each item of RecyclerView
        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView txtCustomerName, txtBuildItem, txtAssignedTo;
            public ImageButton btnPDF;
            public Button btnAssign;
            public CardView cv;

            public ViewHolder(View itemView) {
                super(itemView);
                txtCustomerName = (TextView) itemView.findViewById(R.id.tvCustomerName);
                txtBuildItem = (TextView) itemView.findViewById(R.id.tvBuildItem);
                txtAssignedTo = (TextView) itemView.findViewById(R.id.tvAssignedTo);
                btnPDF = (ImageButton) itemView.findViewById(R.id.btnPDF);
                btnAssign = (Button) itemView.findViewById(R.id.btnAssign);
                cv = (CardView) itemView.findViewById(R.id.cv);
            }
        }

        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            if (buildItems != null) {
                return buildItems.size();
            }
            return 0;
        }

        public List<BuildItem> getBuildList() {
            return buildItems;
        }
    }
