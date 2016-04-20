package it.fold.remotecontrolandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

/**
 * Created by Dhruv on 4/3/2016.
 */
public class DrawerListAdapter extends BaseAdapter {

    private final Context context;
    private final String[] data;
    private final LayoutInflater inflater;
    private final GameView gameView;

    public DrawerListAdapter(Context context,String[] data, GameView view) {
        this.context = context;
        this.data = data;
        this.gameView = view;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.drawer_list_row, null);
        ImageButton left = (ImageButton) vi.findViewById(R.id.imageButton);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.onLeftClickButton(v);
            }
        });
        ImageButton middle = (ImageButton) vi.findViewById(R.id.imageButton2);
        middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.onMiddleClickButton(v);
            }
        });
        ImageButton right = (ImageButton) vi.findViewById(R.id.imageButton3);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.onRightClickButton(v);
            }
        });
        ImageButton keyboard = (ImageButton) vi.findViewById(R.id.imageButton4);
        keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.bringUpKeyboard(v);
            }
        });
        return vi;
    }
}
