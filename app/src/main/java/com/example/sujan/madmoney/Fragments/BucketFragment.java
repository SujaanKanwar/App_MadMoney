package com.example.sujan.madmoney.Fragments;


import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.AppData.Money;
import com.example.sujan.madmoney.Connectors.Constants;
import com.example.sujan.madmoney.MainActivity;
import com.example.sujan.madmoney.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BucketFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bucket, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView totalAmountTextView = (TextView) getActivity().findViewById(R.id.totalMoney);

        ImageView bucket = (ImageView) getActivity().findViewById(R.id.bucketImageView);

        bucket.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ClipData.Item item = new ClipData.Item(new String(Constants.BUCKET_TRANSFER));

                String[] mineType = new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData("MONEY_TRANSFER", mineType, item);

                v.startDrag(dragData,
                        new View.DragShadowBuilder(v),
                        null,
                        0
                );

                return true;
            }
        });

        bucket.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                CharSequence dragData;
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            //v.setColorFilter(Color.BLUE);
                            v.invalidate();
                            return true;
                        }
                        return false;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        //v.setColorFilter(Color.GREEN);
                        v.invalidate();
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        //v.setColorFilter(Color.BLUE);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DROP:
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        Integer amount = Integer.parseInt(item.getText().toString());
                        addAmountToBucket(amount);
                        totalAmountTextView.setText("" + getBucketTotalAmount());
                        ((MainActivity) getActivity()).refreshMoneyView();
                        //v.clearColorFilter();
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        //v.clearColorFilter();
                        v.invalidate();
                        if (event.getResult()) {
                            Toast.makeText(getActivity().getApplicationContext(), "The drop was handled.", Toast.LENGTH_LONG);

                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "The drop didn't work.", Toast.LENGTH_LONG);
                        }
                        return true;
                    default:
                        Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                        break;
                }
                return false;
            }
        });
    }

    private int getBucketTotalAmount() {
        int totalAmount = 0;
        HashMap<Integer, List<Money>> bucketCollection = GlobalStatic.getBucketCollection();
        if (bucketCollection != null)
            for (Integer key : bucketCollection.keySet()) {
                for (Money money : bucketCollection.get(key)) {
                    totalAmount += money.getValue();
                }
            }
        return totalAmount;
    }

    public static void addAmountToBucket(Integer amount) {
        HashMap<Integer, List<Money>> bucketCollection = GlobalStatic.getBucketCollection();
        HashMap<Integer, List<Money>> moneyCollection = GlobalStatic.getMoneyCollection();

        List<Money> moneyList = moneyCollection.get(amount);
        Money money = moneyList.get(0);

        if (bucketCollection == null) {
            bucketCollection = new HashMap<Integer, List<Money>>();
        }

        //add to bucket
        if (!bucketCollection.containsKey(amount)) {
            List<Money> list = new ArrayList<Money>();
            list.add(money);
            bucketCollection.put(amount, list);
        } else {
            bucketCollection.get(amount).add(money);
        }

        //remove from collection
        moneyList.remove(0);
        if (moneyList.size() == 0)
            moneyCollection.remove(amount);

        GlobalStatic.setBucketCollection(bucketCollection);
        GlobalStatic.setMoneyCollection(moneyCollection);
    }

    public static JSONArray getJSONMoneyToTransferFromBucket() {
        JSONArray jsonArray = new JSONArray();
        HashMap<Integer, List<Money>> bucketMoney = GlobalStatic.getBucketCollection();
        if(bucketMoney == null)
            return null;
        for (Integer key : bucketMoney.keySet()) {
            for (Money money : bucketMoney.get(key)) {
                jsonArray.put(money.toJSON());
            }
        }
        return jsonArray;
    }
}
