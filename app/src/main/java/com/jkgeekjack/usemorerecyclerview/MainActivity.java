package com.jkgeekjack.usemorerecyclerview;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
