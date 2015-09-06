package com.example.slideexpandlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ExpandableListView;

public class MainActivity extends Activity {

	private ExpandableListView listView;
	
	private ExpandableAdapter adapter;
	List<String> groups = new ArrayList<String>();
	Map<String , List<String>> children = new HashMap<String, List<String>>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        listView = (ExpandableListView) findViewById(R.id.list_view);
        listView.setGroupIndicator(getResources().getDrawable(R.drawable.ic_launcher));
        //group
        groups.add("字母");
        groups.add("数字");
        groups.add("符号");
        //child
        List<String> child1 = new ArrayList<String>();
        child1.add("a,s,d");
        child1.add("d,f,g");
        child1.add("a,s,d");
        child1.add("t,r,t");
        children.put("字母", child1);
        
        List<String> child2 = new ArrayList<String>();
        child2.add("1,2,3");
        child2.add("4,5,6");
        child2.add("7,6,5");
        children.put("数字", child2);
        
        List<String> child3 = new ArrayList<String>();
        child3.add("＃,^,^");
        child3.add("^,_,^");
        children.put("符号", child3);
        
        adapter = new ExpandableAdapter(this, groups, children);
        listView.setAdapter(adapter);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
