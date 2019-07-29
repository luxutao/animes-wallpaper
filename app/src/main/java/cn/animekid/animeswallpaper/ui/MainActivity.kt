package cn.animekid.animeswallpaper.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.BasicResponse
import cn.animekid.animeswallpaper.data.UserInfoData
import cn.animekid.animeswallpaper.fragment.FragmentBing
import cn.animekid.animeswallpaper.fragment.FragmentPC
import cn.animekid.animeswallpaper.fragment.FragmentPhone
import cn.animekid.animeswallpaper.fragment.FragmentUpload
import cn.animekid.animeswallpaper.utils.ToolsHelper
import cn.animekid.animeswallpaper.utils.database
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.db.delete
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseAAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var isExit: Boolean = false
    private var currentFragment: Fragment? = null
    private var islogin: Boolean = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA)
    private var handler: Handler = @SuppressLint("HandlerLeak")
    object: Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            isExit = false
        }
    }
    private lateinit var navView: NavigationView
    private lateinit var navheaderView: View
    private lateinit var UserAvatar: ImageView
    private lateinit var UserName: TextView
    private lateinit var UserEmail: TextView
    private lateinit var UserProfile: MenuItem
    private lateinit var UserLogout: MenuItem
    private lateinit var BottomMenu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.initUI()

        this.UserAvatar.setOnClickListener {
            if (!this.islogin) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }


        this.BottomMenu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemComputer -> {
                    this.openFragment(FragmentPC.newInstance(), "pc")
                }
                R.id.itemPhone -> {
                    this.openFragment(FragmentPhone.newInstance(), "phone")
                }
                R.id.itemBing -> {
                    this.openFragment(FragmentBing.newInstance(), "bing")
                }
                R.id.itemUpload -> {
                    this.openFragment(FragmentUpload.newInstance(), "upload")
                }
            }
            true
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    fun initUI(){
        this.navView = this.findViewById(R.id.nav_view)
        this.navheaderView = this.navView.inflateHeaderView(R.layout.nav_header_main)
        this.UserAvatar = this.navheaderView.findViewById(R.id.user_avatar)
        this.UserName = this.navheaderView.findViewById(R.id.user_name)
        this.UserEmail = this.navheaderView.findViewById(R.id.user_email)
        this.UserProfile = this.navView.menu.findItem(R.id.nav_profile)
        this.UserLogout = this.navView.menu.findItem(R.id.nav_logout)
        this.BottomMenu = this.findViewById(R.id.bottom_menu)
        // 申请权限
        this.RequestPermission()
        this.openFragment(FragmentPC.newInstance(), "pc")
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 100)
            }
            R.id.nav_gallery -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setType("image/*")
                startActivityForResult(intent, 101)
            }
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_manage -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_share -> {
                val dialogView = View.inflate(this, R.layout.share_dialog, null)
                AlertDialog.Builder(this).setTitle("提示").setView(dialogView)
                    .setPositiveButton("确认") { t_dialog, which -> }
                    .setNegativeButton("取消", null)
                    .create().show()
            }
            R.id.nav_logout -> {
                AlertDialog.Builder(this).setTitle("提示").setMessage("确认注销当前账号吗？")
                    .setPositiveButton("确认") { t_dialog, which ->
                        Requester.AuthService().authLogout(token = ToolsHelper.getToken(this@MainActivity), authtoken = this.UserInfo.token).enqueue(object: Callback<BasicResponse> {
                            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                                this@MainActivity.database.use { delete("anime_users") }
                                Log.d("logoutSuccess","success")
                                this@MainActivity.defaultData(true)
                                Toast.makeText(this@MainActivity, "注销账号成功", Toast.LENGTH_SHORT).show()
                            }

                            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                                Log.d("logoutError",t.message)
                            }
                        })

                    }
                    .setNegativeButton("取消", null)
                    .create().show()
            }
            R.id.nav_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun defaultData(default: Boolean) {
        if (default) {
            // 注销账号后将动态数据还原
            this.UserAvatar.setImageResource(R.drawable.default_avatar)
            this.UserEmail.text = getString(R.string.nav_header_subtitle)
            this.UserName.text = getString(R.string.nav_header_title)
            // 获取侧边栏下面的按钮,设置隐藏属性
            this.UserLogout.isVisible = false
            this.UserProfile.isVisible = false
            this.islogin = false
        } else {
            if (this.UserInfo.avatar != "F") {
                Glide.with(this).load(this.UserInfo.avatar).into(this.UserAvatar)
            }
            this.UserEmail.text = this.UserInfo.email
            this.UserName.text = this.UserInfo.name
            this.UserLogout.isVisible = true
            this.UserProfile.isVisible = true
            this.islogin = true
        }
    }

    override fun onResume() {
        super.onResume()
        this.getData()
        if (this.UserInfo.userid != 0) {
            this.defaultData(false)
        } else {
            this.defaultData(true)
        }
    }

    // 切换fragment保存状态
    private fun openFragment(fragment: Fragment, tag: String) {
        // 如果当前的frag存在就隐藏
        if (currentFragment != null) {
            supportFragmentManager.beginTransaction().hide(currentFragment!!).commit()
        }
        // 如果搜索到了要打开的frag则显示，否则则创建
        currentFragment = supportFragmentManager.findFragmentByTag(tag)
        if (currentFragment == null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragment, tag)
            transaction.commit()
            currentFragment = fragment
        } else {
            supportFragmentManager.beginTransaction().show(currentFragment!!).commit()
        }

    }


    // 我的请求权限
    private fun RequestPermission() {
        //可以添加多个权限申请
        val permissions: ArrayList<String> = arrayListOf()
        val _permissions: Array<String> = arrayOf()
        for (permission in this.permissions) {
            if (ContextCompat.checkSelfPermission(getApplication(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(permission)
            }
        }
        if (permissions.size != 0) {
            requestPermissions(permissions.toArray(_permissions),1)
        }
    }

    // 重写请求权限结果
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            for (permiss in grantResults) {
                if (permiss == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this@MainActivity,"权限拒绝",Toast.LENGTH_SHORT).show()
                    System.exit(0)
                }
            }
        }
    }

    // 重写物理按键，如果要退出则执行判断
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exit()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    // 两次退出则退出
    private fun exit() {
        if (!isExit){
            isExit = true
            Toast.makeText(getApplicationContext(),"再按一次退出程序",Toast.LENGTH_SHORT).show()
            //利用handler延迟发送更改状态信息
            handler.sendEmptyMessageDelayed(0,2000)
        } else{
            finish()
            System.exit(0)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras.get("data") as Bitmap
            MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "" , "")
        }
    }
}
