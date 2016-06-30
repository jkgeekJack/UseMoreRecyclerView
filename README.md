# UseMoreRecyclerView
RecyclerView下拉刷新和万能Adapter
在日常开发中简单地摆放已有数据基本不太可能，往往是要从数据库或者网络中获取然后添加到UI，这时下拉和上拉刷新肯定是家常便饭了，不用恐惧，其实这个也是非常简单的
下拉刷新其实就是用到之前我有提到的[SwipeRefreshLayout](http://blog.csdn.net/qq_32198277/article/details/51498660)，至于上拉刷新就是在RecyclerView底部加一个item，不同item一般都是要不同的viewholder的

还有那个万能的Adapter主要是采用了泛型，可以传入任何数据类型
然后ViewHolder封装了绑定控件的方法，使用起来也是非常简单

现在开讲吧
##**1.导入design包**

```
compile 'com.android.support:design:23.4.0'
```

##**2.写主布局**

```
<?xml version="1.0" encoding="utf-8"?>
<android.support.v4.widget.SwipeRefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:id="@+id/srLayout"
    tools:context="com.jkgeekjack.usemorerecyclerview.MainActivity">
    <android.support.v7.widget.RecyclerView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/recyclerView">

    </android.support.v7.widget.RecyclerView>
</android.support.v4.widget.SwipeRefreshLayout>

```
##**3.写每一个item的布局**

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="horizontal" android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="@dimen/activity_vertical_margin">
    <ImageView
        android:layout_width="50dip"
        android:layout_height="50dip"
        android:src="@drawable/logo"/>
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Name"
        android:textSize="35dip"
        android:id="@+id/tvName"/>
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Age"
        android:textSize="20dip"
        android:id="@+id/tvAge"/>
</LinearLayout>
```
##**4.写底部item的布局**

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/tvState"
        android:text="Loading..."
        android:padding="10dp"
        android:gravity="center"/>
</LinearLayout>
```

##**5.写ViewHolder**
其实这个ViewHolder功能并不复杂，根据id获取控件，和摆放数据的具体实现

```
public class ViewHolder extends RecyclerView.ViewHolder {
//    SparseArray类似HaspMap不过比HashMap节省空间，方法都差不多，id和控件对应
    private SparseArray<View> mViews;
    private View mConvertView;

    public ViewHolder( View itemView)
    {
        super(itemView);
        mViews = new SparseArray<View>();
        mConvertView=itemView;
    }

    //根据id和内容进行摆放
    public void setText(int viewId,String msg){
        TextView textView=getView(viewId);
        textView.setText(msg);
    }
    
    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
}

```

####自己还可以增添例如setBitmap或者设置监听之类的方法
```
 public void setBitmap(int viewId, Bitmap bitmap){
        ImageView imageView=getView(viewId);
        imageView.setImageBitmap(bitmap);
    }
    public void setOnclickListener(int viewId, View.OnClickListener onClickListener){
        View view=getView(viewId);
        view.setOnClickListener(onClickListener);
    }
```

##**6.写万能的Adapter**
虽说是万能也比较简单，有一些特殊点的内容要在用到的时候在具体写，我这里用了抽象的方法

```
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;


    public CommonAdapter(Context context, int layoutId, List<T> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        if (viewType == TYPE_FOOTER) {
            View view = mInflater.inflate(R.layout.item_footer, parent, false);
            return new FooterViewHolder(view);
        } else {
            View view = mInflater.inflate(mLayoutId, parent, false);
            return new ViewHolder(view);
        }


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < mDatas.size()) {
            convert((ViewHolder) holder, mDatas.get(position));
        }
    }
//    摆放数据只能在用的时候才写
    public abstract void convert(ViewHolder holder, T t);

    @Override
    public int getItemCount() {
        return mDatas.size() + 1;
    }

    //是底部iewType为TYPE_FOOTER，不是底部viewType为TYPE_ITEM
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

//    底部的ViewHolder
    class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);

        }

    }
}

```

##**7.写逻辑代码**
逻辑代码并不难，之前我的几篇博客都有提到相关的用法。
不懂RecyclerVIew怎么用的，请戳
[安卓日记——玩转Material Design（RecyclerView+CardView篇）](http://blog.csdn.net/qq_32198277/article/details/51706454)

```
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<UserBean> data = new ArrayList<UserBean>();
    private int lastItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //生成数据
        initVar();
        initView();
    }

    private void initView() {
        //这个Adapter传进去的UserBean的List
        //因为是万能的Adapter，所以摆放数据只能在用的时候才写
        final CommonAdapter<UserBean> adapter = new CommonAdapter<UserBean>(this, R.layout.item_user, data) {
            @Override
            public void convert(ViewHolder holder, UserBean userBean) {
                holder.setText(R.id.tvName, userBean.getName());
                holder.setText(R.id.tvAge, userBean.getAge() + "");
            }
        };
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //添加数据
                        initVar();
                        try {
//                            模拟加载耗时
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                        子线程中改变UI要runOnUiThread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                使加载圆环消失
                                swipeRefreshLayout.setRefreshing(false);
                                //告知Adapter更新数据
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "加载完成", Toast.LENGTH_SHORT).show();


                            }
                        });

                    }
                }).start();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //recyclerView停下来而且可见的item的position是最后一个
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition + 1 == adapter.getItemCount()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            initVar();
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(MainActivity.this, "加载完成", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).start();
                }
            }

            //滚动时获取最后一个可见item的position
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastItemPosition = manager.findLastVisibleItemPosition();
            }
        });
    }

    private void initVar() {
        for (int i = 0; i < 10; i++) {
            data.add(new UserBean("jack" + i, i));
        }
    }
}
```

###一个看上去还算主流的RecyclerView就这样大功告成啦，是不是有点小激动呢？
