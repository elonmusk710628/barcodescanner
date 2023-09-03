package ml.guru.barcodescanner;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class CustomArrayAdapter<T> extends ArrayAdapter<T> {

    private int textColor;

    public CustomArrayAdapter(Context context, int resource, List<T> objects, int textColor) {
        super(context, resource, objects);
        this.textColor = textColor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setTextColor(textColor);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setTextColor(textColor);
        return textView;
    }
}
