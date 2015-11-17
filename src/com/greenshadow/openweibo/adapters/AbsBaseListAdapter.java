package com.greenshadow.openweibo.adapters;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

@SuppressLint("UseSparseArrays")
public abstract class AbsBaseListAdapter<E> extends BaseAdapter {
	public List<E> list;

	public Context mContext;

	public LayoutInflater mInflater;

	public AbsBaseListAdapter(Context context, List<E> list) {
		super();
		this.mContext = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public void add(E e) {
		this.list.add(e);
		notifyDataSetChanged();
	}

	public void addAll(List<E> list) {
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	public void addAll(int position, List<E> list) {
		this.list.addAll(position, list);
		notifyDataSetChanged();
	}

	public void remove(int position) {
		this.list.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = bindView(position, convertView, parent);
		return convertView;
	}

	public abstract View bindView(int position, View convertView, ViewGroup parent);
}
