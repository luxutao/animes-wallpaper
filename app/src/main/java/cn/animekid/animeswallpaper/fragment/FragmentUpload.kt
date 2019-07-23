package cn.animekid.animeswallpaper.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.BasicResponse
import cn.animekid.animeswallpaper.utils.PhotoAlobum
import cn.animekid.animeswallpaper.utils.ToolsHelper
import com.bm.library.PhotoView
import com.bumptech.glide.Glide
import retrofit2.Callback
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class FragmentUpload: Fragment() {

    private lateinit var uploadPath: String
    private lateinit var previewImage: PhotoView
    private lateinit var choiceImage: ImageButton
    private lateinit var rechoice: Button
    private lateinit var upload: Button
    private lateinit var buttonGroup: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_upload, container, false)
        this.initUI(view)
        this.choiceImage.setOnClickListener { goPhotoAlbum() }
        this.rechoice.setOnClickListener { goPhotoAlbum() }
        this.upload.setOnClickListener {
            Toast.makeText(view.context, "正在上传,请稍后~", Toast.LENGTH_SHORT).show()
            val file = File(uploadPath)
            // 上传图片
            val photoRequestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
            val photoPart = MultipartBody.Part.createFormData("file", file.name, photoRequestBody)
            Requester.ImageService().uploadImage(token = ToolsHelper.getToken(view.context), file = photoPart).enqueue(object: Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    Toast.makeText(view.context, "上传成功，谢谢你提供的资源！😘", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.d("upload", t.message)
                }
            })
        }
        return view
    }

    fun initUI(view: View) {
        this.previewImage = view.findViewById(R.id.preview_image)
        this.choiceImage = view.findViewById(R.id.choice_image)
        this.rechoice = view.findViewById(R.id.rechoice)
        this.upload = view.findViewById(R.id.begin_upload)
        this.buttonGroup = view.findViewById(R.id.button_group)
        this.previewImage.enable()
    }

    companion object {
        fun newInstance(): FragmentUpload = FragmentUpload()
    }

    //激活相册操作
    private fun goPhotoAlbum() {
        val intent = Intent()
        intent.setAction(Intent.ACTION_PICK)
        intent.setType("image/*")
        startActivityForResult(intent,2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null) {
            this.uploadPath = PhotoAlobum().getRealPathFromUri(this.context!!, data.data).toString()
            Glide.with(this.view!!).load(this.uploadPath).into(this.previewImage)
            this.previewImage.visibility = View.VISIBLE
            this.choiceImage.visibility = View.GONE
            this.buttonGroup.visibility = View.VISIBLE
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}