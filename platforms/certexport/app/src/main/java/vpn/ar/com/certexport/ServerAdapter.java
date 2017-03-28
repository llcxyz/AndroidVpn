package vpn.ar.com.certexport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ar.vpn.Server;

import java.util.List;

/**
 * Created by aron on 2016/10/19.
 */
public class ServerAdapter extends BaseAdapter{
  private List<Server> serverList;
  private Context context;

  public ServerAdapter(Context context, List<Server> serverList){
    this.context = context;
    this.serverList = serverList;

  }
  @Override
  public int getCount() {
    return serverList.size();
  }

  @Override
  public Object getItem(int position) {
    return serverList.get(position);
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
      _TextView1.setText(serverList.get(position).getName());
      _TextView2.setText(""); //no server ip
    }
    return convertView;
  }
}
