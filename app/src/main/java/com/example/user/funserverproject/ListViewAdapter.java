package com.example.user.funserverproject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;


public class ListViewAdapter extends BaseAdapter implements Serializable {
    private static final long serialVersionUID = 1L;

    // Adapter에 추가된 데이터 저장.
    private ArrayList<ListViewItem> mItemList = new ArrayList<>();

    // 생성자
    public ListViewAdapter() {}

    // Adapter의 데이터 개수 리턴.
    @Override
    public int getCount(){
        return mItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는 데 사용하는 View 리턴.
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_oneitem, parent, false);
        }

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageIcon);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textName);
        TextView descTextView = (TextView) convertView.findViewById(R.id.textNum);

        iconImageView.setImageDrawable(mItemList.get(pos).getIcon());
        titleTextView.setText(mItemList.get(pos).getName());
        descTextView.setText(mItemList.get(pos).getPhoneNum());

        return convertView;
    }

    // 지정한 위치에 있는 데이터와 관계된 아이템의 ID를 리턴.
    @Override
    public long getItemId(int position){
        return position;
    }

    // 지정한 위치에 있는 데이터 리턴.
    @Override
    public ListViewItem getItem(int position){
        return mItemList.get(position);
    }

    public ArrayList<ListViewItem> getItemList() {
        return mItemList;
    }

    // listView에 담을 아이템 추가
    public void addItem(Drawable icon, String title, String desc){
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setName(title);
        item.setPhoneNum(desc);

        mItemList.add(item);
    }

    // 중복 연락처 로딩 방지
    public boolean isDuplicate(String name, String phoneNum){
        for (int i = 0; i < this.getCount(); i++){
            if ((mItemList.get(i).getName().equals(name))
                    && (mItemList.get(i).getPhoneNum().equals(phoneNum))){
                return true;
            }
        }
        return false;
    }
}
