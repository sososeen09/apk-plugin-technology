package com.sososeen09.host.mergeway;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sososeen09.host.HostSecondActivity;
import com.sososeen09.host.R;
import com.sososeen09.host.utils.PackageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MergeClassLoaderActivity extends AppCompatActivity {

    private PluginAdapter pluginAdapter;
    private RecyclerView rv_plugin_apk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_class_loader);
        rv_plugin_apk = findViewById(R.id.rv_plugin_apk);

        pluginAdapter = new PluginAdapter(getData(), this);
        rv_plugin_apk.setLayoutManager(new LinearLayoutManager(this));
        rv_plugin_apk.setAdapter(pluginAdapter);

        MergedPluginHelper.init(this);
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void jumpHost(View view) {
        startActivity(new Intent(this, HostSecondActivity.class));
    }


    private List<PluginItem> getData() {
        String pluginFolder = Environment.getExternalStorageDirectory().toString() + "/apk_plugin_dir";
        File file = new File(pluginFolder);
        File[] plugins = file.listFiles();
        if (plugins == null || plugins.length == 0) {
            return new ArrayList<>();
        }

        ArrayList<PluginItem> mPluginItems = new ArrayList<>();
        for (File plugin : plugins) {
            PluginItem item = new PluginItem();
            item.pluginPath = plugin.getAbsolutePath();
            item.packageInfo = PackageUtils.getPackageInfo(this, item.pluginPath);
            if (item.packageInfo.activities != null && item.packageInfo.activities.length != 0) {
                item.launcherActivityName = item.packageInfo.activities[0].name;
            }
            if (item.packageInfo.services != null && item.packageInfo.services.length != 0) {

                item.launcherServiceName = item.packageInfo.services[0].name;
            }
            mPluginItems.add(item);
        }

        return mPluginItems;
    }

    private class PluginAdapter extends RecyclerView.Adapter<Holder> {

        private final List<PluginItem> data;
        private final Context context;

        public PluginAdapter(List<PluginItem> data, Context context) {
            this.data = data;
            this.context = context;

        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plugin_apk, parent, false);
            Holder holder = new Holder(view);
            holder.btnLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    PluginItem pluginItem = pluginAdapter.data.get(position);
                    try {
                        MergedPluginHelper.getInstance().mergePlugin(pluginItem.pluginPath);
                        showToast("加载完毕: " + position);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("加载失败: " + position + " \n " + e.getMessage());

                    }
                }
            });
            holder.btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    PluginItem pluginItem = data.get(position);
                    Intent intent = new Intent();
                    ComponentName component = new ComponentName(pluginItem.packageInfo.packageName, pluginItem.launcherActivityName);
                    intent.setComponent(component);
                    context.startActivity(intent);
                }
            });
            return holder;
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void onBindViewHolder(Holder holder, int position) {
            PluginItem pluginItem = data.get(position);
            holder.appIcon.setImageDrawable(PackageUtils.getAppIcon(context, pluginItem.pluginPath));
            holder.appName.setText(PackageUtils.getAppLabel(context, pluginItem.pluginPath));
            holder.apkName.setText(String.format("%s%d", pluginItem.pluginPath.substring(pluginItem.pluginPath.lastIndexOf(File.separatorChar)), 1));

            holder.packageName.setText(pluginItem.packageInfo.applicationInfo.packageName + "\n" +
                    pluginItem.launcherActivityName + "\n" +
                    pluginItem.launcherServiceName);

            holder.btnLoad.setTag(position);
            holder.btnGo.setTag(position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    class PluginItem {
        PackageInfo packageInfo = null;
        String pluginPath = null;
        String launcherActivityName = null;
        String launcherServiceName = null;
    }

    private class Holder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView apkName;
        TextView packageName;
        Button btnLoad;
        Button btnGo;

        public Holder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            apkName = itemView.findViewById(R.id.apk_name);
            packageName = itemView.findViewById(R.id.package_name);
            btnLoad = itemView.findViewById(R.id.btn_load);
            btnGo = itemView.findViewById(R.id.btn_go);
        }

    }
}
