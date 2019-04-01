package ru.relastic.meet007;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String SERVICE_STARTED = "service started";
    private static final String SERVICE_RND_ID = "service_rnd_id";
    private static final String SERVICE_RND_VALUE = "service_rnd_value";
    private MyService.ProtectedService mService;
    private final IncomingHandler mHandler = new IncomingHandler();
    public ArrayList<Bundle> mData = new ArrayList<>();
    private RecyclerView mRecyclerView=null;
    private RecyclerView.LayoutManager mLayoutManager;
    private CustomAdapter mAdapter;
    private CustomAdapter3 mAdapter3;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MyService.LocalBinder) service).getProtectedService();
            mService.addListener(mHandler);
            if (mData.size()==0) {
                populateData();
            }
            if (mRecyclerView == null) {
                initReciclerView();
            }
            Log.v("LOG:", "SERVICE MyService: Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v("LOG:", "SERVICE MyService: Disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListeners();
        init();
    }
    private void initViews() {
    }
    private void initListeners() {
    }
    private void init() {
        mRecyclerView = null;
        mData.clear();
    }
    private void initReciclerView(){
        mRecyclerView = findViewById(R.id.recicler_view);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //mLayoutManager = new CustomGridLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CustomAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);
    }
    private void populateData(){
        for (int i=1; i<=100; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt(SERVICE_RND_ID,i+1000);
            bundle.putString(SERVICE_RND_VALUE,mService.getNextValue());
            mData.add(bundle);
        }
    }
    private void changeData(Bundle data, Bundle newBundle){
        if (data.getInt(MyViewHolder.HOLDER_INT_DATA_LEVEL)==1) {
            CustomAdapter currentAdapter = mAdapter;
            ArrayList<Bundle> dataNew = dataClone(currentAdapter.data);
            dataNew.set(data.getInt(MyViewHolder.HOLDER_INT_DATA_INDEX),newBundle);
            currentAdapter.onNewData(dataNew);




        }else if(data.getInt(MyViewHolder.HOLDER_INT_DATA_LEVEL)==2) {
            CustomAdapter3 currentAdapter = mAdapter3;
            ArrayList<Bundle> dataNew = dataClone(currentAdapter.data);
            dataNew.set(data.getInt(MyViewHolder.HOLDER_INT_DATA_INDEX),newBundle);
            currentAdapter.onNewData(dataNew);
        }
    }
    private void hasStatusChangeByBind(String newValue) {
        //System.out.println("@@@@@@@@@@@@@@@ "+ mService.getNextValue());
    }
    protected void onPause() {
        mService.removeListener(mHandler);
        unbindService(mServiceConnection);
        super.onPause();
    }
    @Override
    protected void onResume() {
        bindService(MyService.newIntent(MainActivity.this), mServiceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(SERVICE_STARTED,true);
        //savedInstanceState.putString("mTextView",mTextView.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //mTextView.setText(savedInstanceState.getString("mTextView"));
        super.onRestoreInstanceState(savedInstanceState);
    }
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String message = msg.getData().getString(MyService.MSG_SERVICE_RNDVALUE);
            hasStatusChangeByBind(message);
        }
    }

    public class CustomAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private final ArrayList<Bundle> data;
        CustomAdapter(ArrayList<Bundle> mData) {
            data=mData;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            //INFLATER BY TYPE i
            MyViewHolder mvh = null;
            switch (i) {
                case 1:
                    mvh = new MyViewHolder1(LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_type_two,viewGroup,false));
                    break;
                case 2:
                    mvh = new MyViewHolder2(LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_type_three,viewGroup,false));
                    break;
                case 3:
                    mvh = new MyViewHolder3(LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_recicler_view_horizontal,viewGroup,false));
                    break;
                default:
                    mvh = new MyViewHolder0(LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_type_one,viewGroup,false));
                    break;
            }

            return mvh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            //ATTEMPT VALUES BY POSITION i
            if (myViewHolder.isRecyclerViewChild()) {
                RecyclerView recyclerViewChild = myViewHolder.recyclerViewChild;
                LinearLayoutManager mLinearLayoutManager3 = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                recyclerViewChild.setLayoutManager(mLinearLayoutManager3);
                mAdapter3 = new CustomAdapter3(mData);
                recyclerViewChild.setAdapter(mAdapter3);
            }else {
                myViewHolder.textView.setText(data.get(i).getString(SERVICE_RND_VALUE));
                myViewHolder.button.setText("pos "+i);
                Bundle data = new Bundle();
                data.putInt(MyViewHolder.HOLDER_INT_DATA_LEVEL,1);
                data.putInt(MyViewHolder.HOLDER_INT_DATA_INDEX,i);
                myViewHolder.data = data;
            }
        }

        @Override
        public int getItemViewType(int position) {
            int retVal = 0;
            int pos = position+1;
            if (pos == ((int)(pos/7))*7) {
                retVal = 1;
            }else if (pos == ((int)(pos/13))*13) {
                retVal = 2;
            }else if (pos == ((int)(pos/51))*51) {
                retVal = 3;
            }
            return retVal;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        public void onNewData(ArrayList<Bundle> newData){
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffCall(data, newData));
            result.dispatchUpdatesTo(this);
            data.clear();
            data.addAll(newData);
        }
    }

    public class CustomAdapter3 extends RecyclerView.Adapter<MyViewHolder> {
        private final ArrayList<Bundle> data;
        CustomAdapter3(ArrayList<Bundle> mData) {
            data=mData;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return  new MyViewHolderChild(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.layout_type_horizontal_items,viewGroup,false));
        }

        @Override
        public void onBindViewHolder (@NonNull MyViewHolder myViewHolder, int i) {
            myViewHolder.textView.setText(data.get(i).getString(SERVICE_RND_VALUE));
            myViewHolder.button.setText("pos "+i);
            Bundle data = new Bundle();
            data.putInt(MyViewHolder.HOLDER_INT_DATA_LEVEL,2);
            data.putInt(MyViewHolder.HOLDER_INT_DATA_INDEX,i);
            myViewHolder.data = data;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        public void onNewData(ArrayList<Bundle> newData){
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffCall(data, newData));
            result.dispatchUpdatesTo(this);
            data.clear();
            data.addAll(newData);
        }
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        public static final String HOLDER_INT_DATA_LEVEL = "holder_int_data_level";
        public static final String HOLDER_INT_DATA_INDEX = "holder_int_data_index";
        public TextView textView=null;
        public Button button=null;
        public RecyclerView recyclerViewChild=null;
        public Bundle data = new Bundle();
        private final MainActivity activity;

        public MyViewHolder(View itemView, Button btn){
            super(itemView);
            button = btn;
            activity = (MainActivity)itemView.getContext();
            //System.out.println("()()()()()()()()()()(()()()(): "+itemView.getContext().getClass().toString());

            if (button!=null) {
                button.setOnClickListener( new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Button b = (Button)v;
                        //System.out.println("---- CLICK ---- : "+b.getText().toString());
                        Bundle newBundle = new Bundle();
                        newBundle.putInt(SERVICE_RND_ID,data.getInt(HOLDER_INT_DATA_INDEX)+1001);
                        newBundle.putString(SERVICE_RND_VALUE,mService.getNextValue());
                        activity.changeData(data, newBundle);
                    }
                });
            }
        }

        public boolean isRecyclerViewChild() {return recyclerViewChild!=null;}
    }
    public class MyViewHolder0 extends MyViewHolder {
        public MyViewHolder0(View itemView) {
            super(itemView, (Button)itemView.findViewById(R.id.button));
            textView = itemView.findViewById(R.id.textView);
        }
    }
    public class MyViewHolder1 extends MyViewHolder {
        public MyViewHolder1(View itemView) {
            super(itemView, (Button)itemView.findViewById(R.id.button2));
            textView = itemView.findViewById(R.id.textView2);
        }
    }
    public class MyViewHolder2 extends MyViewHolder {
        public MyViewHolder2(View itemView) {
            super(itemView, (Button)itemView.findViewById(R.id.button3));
            textView = itemView.findViewById(R.id.textView3);
        }
    }
    public class MyViewHolder3 extends MyViewHolder {
        public MyViewHolder3(View itemView) {
            super(itemView, null);
            recyclerViewChild = itemView.findViewById(R.id.recicler_view_child);
        }
    }
    public class MyViewHolderChild extends MyViewHolder {
        public MyViewHolderChild(View itemView) {
            super(itemView, (Button)itemView.findViewById(R.id.button4));
            textView = itemView.findViewById(R.id.textView4);
        }
    }

    public class DiffCall extends DiffUtil.Callback {
        private List<Bundle> mOldList;
        private List<Bundle> mNewList;
        public DiffCall(List <Bundle> oldList, List <Bundle> newList){
            mOldList = oldList;
            mNewList = newList;
        }
        @Override
        public int getOldListSize() {
            return mOldList.size();
        }
        @Override
        public int getNewListSize() {
            return mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int i, int i1) {
            boolean retVal = mOldList.get(i).getInt(SERVICE_RND_ID)==mNewList.get(i1).getInt(SERVICE_RND_ID);
            if (!retVal) {
                System.out.println("%%%%%%%%%% "+i+"  :  " + mOldList.get(i).getInt(SERVICE_RND_ID) +" | "+ mNewList.get(i1).getInt(SERVICE_RND_ID));
            }
            return retVal;
        }

        @Override
        public boolean areContentsTheSame(int i, int i1) {
            boolean retVal = mOldList.get(i).getString(SERVICE_RND_VALUE).equals(mNewList.get(i1).getString(SERVICE_RND_VALUE));
            if (!retVal) {
                System.out.println("########## "+i+"  :  " + mOldList.get(i).getString(SERVICE_RND_VALUE) +" | "+ mNewList.get(i1).getString(SERVICE_RND_VALUE));
            }
            return retVal;
        }


    }

    public static ArrayList<Bundle> dataClone(ArrayList<Bundle> data) {
        ArrayList<Bundle> retVal = new ArrayList<>();
        retVal.addAll(data);
        return retVal;
    }
}
