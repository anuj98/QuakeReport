package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake>{

    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> earthquake){
        super(context,0,earthquake);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_view, parent, false);
        }

        Earthquake currentearthquake = getItem(position);
        String str="";

        //Magnitude::
        TextView mag = (TextView) listItemView.findViewById(R.id.magnitude);

        GradientDrawable graphics = (GradientDrawable)mag.getBackground();
        graphics.setColor(getMagColor(currentearthquake.getMag()));

        DecimalFormat dec = new DecimalFormat("0.0");
        str = dec.format(currentearthquake.getMag());
        mag.setText(str);

        /********************-------------------------------*******************/
        /********************-------------------------------*******************/
        //Location::
        str = currentearthquake.getLoc();
        String primary,offset;

        if(str.contains("of")){
           String[] prioff = str.split("of");
            offset = prioff[0]+"of";
            primary = prioff[1];
        }
        else{
            offset = getContext().getString(R.string.near_the);  //default string defined in res->strings.xml
            primary = str;
        }

        TextView off = (TextView) listItemView.findViewById(R.id.offset);
        off.setText(offset);
        TextView loc = (TextView) listItemView.findViewById(R.id.primary);
        loc.setText(primary);


        /**********----------------------------***************************/
        /**********----------------------------***************************/
        /*DATE and TIME::Formatting the date in milliseconds to original date*/
        long milliseconds = currentearthquake.getDate();
        Date obj = new Date(milliseconds);
        SimpleDateFormat dat = new SimpleDateFormat("MMM dd,yyyy");
        str = dat.format(obj);

        TextView date = (TextView) listItemView.findViewById(R.id.date);
        date.setText(str);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        str = timeFormat.format(obj);
        TextView time = (TextView) listItemView.findViewById(R.id.time);
        time.setText(str);

        return listItemView;
    }

    public int getMagColor(double mag){
            int color;
            int m = (int)Math.floor(mag);
            switch(m){
                case 0:
                case 1:
                    color = R.color.magnitude1; break;
                case 2:
                    color = R.color.magnitude2; break;
                case 3:
                    color = R.color.magnitude3; break;
                case 4:
                    color = R.color.magnitude4; break;
                case 5:
                    color = R.color.magnitude5; break;
                case 6:
                    color = R.color.magnitude6; break;
                case 7:
                    color = R.color.magnitude7; break;
                case 8:
                    color = R.color.magnitude8; break;
                case 9:
                    color = R.color.magnitude9; break;
                default:
                    color= R.color.magnitude10plus;
            }
            return ContextCompat.getColor(getContext(),color);
    }
}
