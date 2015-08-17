package howto.sound.shakemeeter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by !13 on 15/08/2015.
 */
public class DrawerAdapter extends ArrayAdapter<Categorie>{
    private ArrayList<Categorie> categories;
    private Context context;
    private int res;

    static class ViewHolder {
        ImageView img;
        TextView categorie;
    }

    public DrawerAdapter(Context context, int resource, ArrayList<Categorie> categories){
        super(context, resource, categories);
        this.context = context;
        this.categories = categories;
        this.res = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(res, parent, false);
            holder = new ViewHolder();
            holder.img = (ImageView) view.findViewById(R.id.drawer_img);
            holder.categorie = (TextView) view.findViewById(R.id.drawer_categorie);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        final Categorie categorie = categories.get(position);

        if (categories != null){
            holder.categorie.setText(categorie.getCategorie());
            holder.img.setImageResource(categorie.getImg());
        }

        return view;
    }

    @Override
    public int getCount(){
        return categories.size();
    }
}
