package vpn.ar.com.certexport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ar.vpn.security.TrustedCertificateEntry;

import java.util.List;

/**
 * Created by aron on 2016/10/18.
 */
public class CertAdapter extends BaseAdapter {
    public List<TrustedCertificateEntry> list;
    public Context context;

    public CertAdapter(Context context, List<TrustedCertificateEntry> list){
        this.list = list;
        this.context = context;

    }
    @Override
    public int getCount() {
         return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater _LayoutInflater=LayoutInflater.from(context);
        convertView=_LayoutInflater.inflate(R.layout.spitem, null);
        if(convertView!=null)
        {
            TextView _TextView1=(TextView)convertView.findViewById(R.id.textView1);
            TextView _TextView2=(TextView)convertView.findViewById(R.id.textView2);
            _TextView1.setText(list.get(position).getSubjectPrimary());
            _TextView2.setText(list.get(position).getSubjectSecondary());

        }
        return convertView;

    }
}
