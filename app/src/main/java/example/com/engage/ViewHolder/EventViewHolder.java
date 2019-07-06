package example.com.engage.ViewHolder;

import android.content.ClipData;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import example.com.engage.Common.Common;
import example.com.engage.Interface.ItemClickListener;
import example.com.engage.R;

public class EventViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener{

    public TextView event_name,event_booking;
    public ImageView event_image,fav_image,share_image,quick_cart;

    private ItemClickListener itemClickListener;

    public EventViewHolder (View itemView) {

        super (itemView);

        event_name = (TextView) itemView.findViewById(R.id.event_name);
        event_booking = (TextView) itemView.findViewById(R.id.event_booking);
        event_image = (ImageView) itemView.findViewById(R.id.event_image);
        //fav_image = (ImageView) itemView.findViewById(R.id.fav);
        share_image = (ImageView) itemView.findViewById(R.id.btnShare);
        //quick_cart = (ImageView) itemView.findViewById(R.id.btn_quick_cart);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view){
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}