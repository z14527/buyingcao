package com.gyg.buyingcao;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rxpermisson.PermissionAppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import rx.Subscriber;

import static java.security.AccessController.getContext;

public class MainActivity extends PermissionAppCompatActivity {

 //   private TextView mTextMessage;
    private BottomNavigationView bottomNavigationView;
    private  CaseFragment caseFragment;
    private  KeyFragment keyFragment;
    private  SearchFragment searchFragment;
    private  ViewFragment viewFragment;
    private Fragment[] fragments;
    private int lastfragment;//用于记录上个选择的Fragment
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String strCaseNum="",strCaseApd="",strCaseClass="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
        checkPermission(R.string.base_permission, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.INTERNET)
                .subscribe(new Subscriber() {
                    @Override
                    public void onNext(Object o) {
                        if (o.equals(true)){
               //             Toast.makeText(MainActivity.this,"请求权限成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this,"请求权限失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                });
    /*    mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);*/
    }
    private void initFragment()
    {
        caseFragment = new CaseFragment();
        keyFragment = new KeyFragment();
        searchFragment = new SearchFragment();
        viewFragment = new ViewFragment();
        fragments = new Fragment[]{caseFragment,keyFragment,searchFragment,viewFragment};
        lastfragment=0;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainview,caseFragment).show(caseFragment).commit();
        bottomNavigationView = findViewById(R.id.navigation);
   //     bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.navigation_case:
                {
                    if(lastfragment!=0)
                    {
                        switchFragment(lastfragment,0);
                        lastfragment=0;
                    }
                    return true;
                }
                case R.id.navigation_key:
                {
                    if(lastfragment!=1)
                    {
                        switchFragment(lastfragment,1);
                        lastfragment=1;
                    }
                    return true;
                }
                case R.id.navigation_search:
                {
                    if(lastfragment!=2)
                    {
                        switchFragment(lastfragment,2);
                        lastfragment=2;
                    }
                    return true;
                }
                case R.id.navigation_view:
                {
                    if(lastfragment!=3)
                    {
                        switchFragment(lastfragment,3);
                        lastfragment=3;
                    }
                    return true;
                }
            }
            return false;
        }
    };
    //切换Fragment
    private void switchFragment(int lastfragment,int index)
    {
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        strCaseNum = pref.getString("CaseNum","");
        if(strCaseNum.length()<3){
            Toast.makeText(this,"申请号格式不对", Toast.LENGTH_LONG).show();
            return;
        }
        if(fragments[index].isAdded()==false)
        {
            transaction.add(R.id.mainview,fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }
}
class MyUtil {

    public Context context;
    MyUtil(Context ct){
        context = ct;
    }
    public String codeString(String fileName) throws Exception {
        BufferedInputStream bin = new BufferedInputStream(
                new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();
        String code = null;

        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }

        return code;
    }
    public String readExternal(String filename) throws IOException {
        String encoding = "GBK";
//        try {
//            encoding = codeString(filename);
//        } catch (Exception e) {
//            Toast.makeText(context,"无法获取文件编码格式:\n" + e.toString(), Toast.LENGTH_LONG).show();
//        }
        StringBuilder sb = new StringBuilder("");
        //打开文件输入流
        FileInputStream inputStream = new FileInputStream(filename);
        byte[] buffer = new byte[1024];
        int len = inputStream.read(buffer);
        //读取文件内容
        while(len > 0){
            sb.append(new String(buffer,0,len,encoding));
            //继续将数据放到buffer中
            len = inputStream.read(buffer);
        }
        //关闭输入流
        inputStream.close();
        return sb.toString();
    }
    public boolean writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        if(makeFilePath(filePath, fileName)==null){
            return false;
        }
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if(file.exists())
                file.delete();
            file.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(0);
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Toast.makeText(context,"Error on write File:\n" + e, Toast.LENGTH_LONG).show();
            return false;
            //   Log.e("TestFile", "Error on write File:" + e);
        }
        return true;
    }
    public boolean writeTxtToFile(String strcontent, String strFilePath) {
        //生成文件夹之后，再生成文件，不然会出错
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if(file.exists())
                file.delete();
            file.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(0);
            raf.write(strContent.getBytes("GBK"));
            raf.close();
        } catch (Exception e) {
            Toast.makeText(context,"Error on write File:\n" + e, Toast.LENGTH_LONG).show();
            return false;
            //   Log.e("TestFile", "Error on write File:" + e);
        }
        return true;
    }

//生成文件

    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,"Error on makeFilePath File:\n" + filePath + fileName + "\n" +e, Toast.LENGTH_LONG).show();
            file = null;
        }
        return file;
    }

//生成文件夹

    public void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            //Log.i("error:", e + "");
        }
    }
    //打开指定文件，读取其数据，返回字符串对象
    public String readFileData(String fileName){
        String result="";
        try{
            FileReader freader = new FileReader(fileName);
            //获取文件长度
            BufferedReader br =new BufferedReader(freader);

/*4.可以调用字符缓冲流br的readLine()方法度一行输入文本*/
            String line =null;
            while((line =br.readLine())!=null){
                result = result + line +"\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,"读文件"+fileName+"失败\n"+e.toString(), Toast.LENGTH_LONG).show();
        }
        return  result;
    }


}
class pf {
    /**
     * @describe 压缩多个文件
     * @author zfc
     * @date 2018年1月11日 下午8:34:00
     */
    public void zipFiles(File[] srcFiles, File zipFile) {
            // 判断压缩后的文件存在不，不存在则创建
            if (!zipFile.exists()) {
                try {
                    zipFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 创建 FileOutputStream 对象
            FileOutputStream fileOutputStream = null;
            // 创建 ZipOutputStream
            ZipOutputStream zipOutputStream = null;
            // 创建 FileInputStream 对象
            FileInputStream fileInputStream = null;

            try {
                // 实例化 FileOutputStream 对象
                fileOutputStream = new FileOutputStream(zipFile);
                // 实例化 ZipOutputStream 对象
                zipOutputStream = new ZipOutputStream(fileOutputStream);
                // 创建 ZipEntry 对象
                ZipEntry zipEntry = null;
                // 遍历源文件数组
                for (int i = 0; i < srcFiles.length; i++) {
                    // 将源文件数组中的当前文件读入 FileInputStream 流中
                    fileInputStream = new FileInputStream(srcFiles[i]);
                    // 实例化 ZipEntry 对象，源文件数组中的当前文件
                    zipEntry = new ZipEntry(srcFiles[i].getName());
                    zipOutputStream.putNextEntry(zipEntry);
                    // 该变量记录每次真正读的字节个数
                    int len;
                    // 定义每次读取的字节数组
                    byte[] buffer = new byte[1024];
                    while ((len = fileInputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }
                }
                zipOutputStream.closeEntry();
                zipOutputStream.close();
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
      }
    @SuppressWarnings("null")
    public void writefile(String filename, String code, String[] text){
        BufferedWriter wd = null;
        try {
            try {
                FileOutputStream fos = new FileOutputStream(filename);
                if(code.indexOf("utf")>=0 ||code.indexOf("UTF")>=0){
                    try {
                        fos.write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        try {
                            fos.close();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
                wd = new BufferedWriter(new OutputStreamWriter(fos,code));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                try {
                    wd.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            for(int i = 0; i < text.length; i++){
                String t1 = text[i];
                if(t1!=null && !t1.isEmpty())
                    wd.write((String)text[i]+"\r\n");
                wd.flush();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            wd.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void appendfile(String filename, String code, String[] text){
        BufferedWriter wd = null;
        try {
            try {
                wd = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(filename,true),code));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            System.out.println(text.length);
            for(int i = 0; i < text.length; i++){
                String t1 = text[i];
                if(t1!=null && !t1.isEmpty())
                    wd.append((String)text[i]+"\r\n");
                wd.flush();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            wd.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void appendfile(String filename, String code, String text){
        BufferedWriter wd = null;
        try {
            try {
                wd = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(filename,true),code));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            System.out.println(text);
            wd.append(text+"\r\n");
            wd.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            wd.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public String[] readfile(String filename, String code){
        BufferedReader rd = null;
        String ret[] = null;
        File f1 = new File(filename);
        if(!f1.exists())
            return ret;
        ArrayList <String> info = new ArrayList<String>();
        FileInputStream fis = null;
        try {
            try {
                fis = new FileInputStream(filename);
                rd = new BufferedReader(new InputStreamReader(fis,code));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if(code.indexOf("utf")>=0 ||code.indexOf("UTF")>=0){
            try {
                fis.read(new byte[3]);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String ts = null;
        try {
            while((ts = rd.readLine())!=null){
                if(!ts.isEmpty() && ts!=null)
                    info.add(ts);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            rd.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ret = new String[info.size()];
        for(int i = 0; i < info.size(); i++)
            ret[i] = (String)info.get(i);
        return ret;
    }

    public void Dcmd(String cmd){
        String scs = cmd;
        Process p = null;
        System.out.println(scs);
        try {
            p = Runtime.getRuntime().exec(scs);
            new Thread(new StreamDrainer(p.getInputStream())).start();
            new Thread(new StreamDrainer(p.getErrorStream())).start();
//			p.getInputStream().close();
//			p.getErrorStream().close();
//			p.getOutputStream().close();
//			p.waitFor();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
        }
    }
    public void Dcmd(String cmd, boolean f1 ){
        String scs = cmd;
        Process p = null;
        System.out.println(scs);
        try {
            p = Runtime.getRuntime().exec(scs);
            new Thread(new StreamDrainer(p.getInputStream())).start();
            p.getOutputStream().close();
            if(f1)
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
        }
    }
    /**
     * 获取目录下所有文件(按时间排序)
     *
     * @param path
     * @return
     */
    public List<File> listFileSortByModifyTime(String path) {
        List<File> list = getFiles(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        return list;
    }

    /**
     *
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }
}
class StreamDrainer implements Runnable{
    private InputStream ins;
    public StreamDrainer(InputStream ins){
        this.ins = ins;
    }
    public void run(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            String line = null;
            while((line =  reader.readLine())!=null){
                System.out.println(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}