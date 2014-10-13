package com.pauselabs.pause.ui;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.models.Interaction;
import com.pauselabs.pause.models.Node;
import com.pauselabs.pause.models.NodeV2;
import com.pauselabs.pause.models.Option;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;


public class InteractionFragment extends Fragment implements ListView.OnItemClickListener {
	private static final String TAG = InteractionFragment.class.getSimpleName();
	private Interaction mInteraction;
	private NodeV2 curNode;

	private static final String ARG_FILENAME = "ARG_FILENAME";

	private OnFragmentInteractionListener mListener;

	@InjectView(R.id.frag_interaction_tv) TextView mContent;
	@InjectView(R.id.frag_interaction_lv) ListView mListView;
	@InjectView(R.id.btn_start_over) Button mStartOver;
	@InjectView(R.id.btn_opt_1) Button mOption1;
	@InjectView(R.id.btn_opt_2) Button mOption2;
	@InjectView(R.id.frag_interaction_et) EditText mUserInput;

	private Adapter mAdapter;

	private NodeV2.OptionV2 opt1;
	private NodeV2.OptionV2 opt2;

	public static InteractionFragment newInstance(String filename) {
		InteractionFragment fragment = new InteractionFragment();
		Bundle args = new Bundle();
		args.putString(ARG_FILENAME, filename);
		fragment.setArguments(args);
		return fragment;
	}
	public InteractionFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			String filename = getArguments().getString(ARG_FILENAME);
			mInteraction = new Interaction(getActivity(), filename);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_interaction, container, false);
		Views.inject(this, view);
		mListView.setOnItemClickListener(this);

		mStartOver.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showNode(mInteraction.getStart());
			}
		});

		mOption1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showNode(mInteraction.getNode(opt1.getNext()));
			}
		});

		mOption2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showNode(mInteraction.getNode(opt2.getNext()));
			}
		});

		mUserInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH ||
					actionId == EditorInfo.IME_ACTION_DONE ||
					event.getAction() == KeyEvent.ACTION_DOWN &&
					event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					if (!event.isShiftPressed()) {
						showNode(mInteraction.getNode(curNode.getDefault().getNext()));
						return true; // consume.
					}
				}
				return false; // pass on to other listeners.
			}
		});

		if (mInteraction == null) {
			mInteraction = new Interaction(getActivity(), "InteractionV2.json");
		}
		showNode(mInteraction.getStart());
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

	private void showNode(NodeV2 node) {
		curNode = node;


		mContent.setText(curNode.getContent());

		if (curNode.isNormal()) {
			mListView.setVisibility(View.GONE);
			mUserInput.setVisibility(View.GONE);
			mOption1.setVisibility(View.VISIBLE);
			mOption2.setVisibility(View.VISIBLE);

			ArrayList<NodeV2.OptionV2> options = curNode.getOptions();

			opt1 = options.get(0);
			mOption1.setText(opt1.getContent());

			opt2 = options.get(1);
			mOption2.setText(opt2.getContent());

		} else if (curNode.isSelectOne()) {
			mListView.setVisibility(View.VISIBLE);
			mUserInput.setVisibility(View.GONE);
			mOption1.setVisibility(View.GONE);
			mOption2.setVisibility(View.GONE);
			if (mAdapter == null) {
				mAdapter = new Adapter();
				mListView.setAdapter(mAdapter);
			}
			mAdapter.setOptions(curNode.getChoices());
		} else if (curNode.isUserInput()) {
			mListView.setVisibility(View.GONE);
			mUserInput.setVisibility(View.VISIBLE);
			mOption1.setVisibility(View.GONE);
			mOption2.setVisibility(View.GONE);


		} else {
			mListView.setVisibility(View.GONE);
			mUserInput.setVisibility(View.GONE);
			mOption1.setVisibility(View.GONE);
			mOption2.setVisibility(View.GONE);
		}


	}


	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		NodeV2 next = mInteraction.getNode(curNode.getDefault().getNext());
		if (next != null)
			showNode(next);
	}

	private class Adapter extends BaseAdapter {
		private ArrayList<String> mOptions = new ArrayList<String>();

		@Override
		public String getItem(int i) {
			return mOptions.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = new TextView(getActivity());
			}
			((TextView) view).setText(getItem(i));
			((TextView) view).setTextColor(Color.BLACK);
			return view;
		}

		public void setOptions(ArrayList<String> options) {
			mOptions = options;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mOptions.size();
		}
	}

}
