package me.m123.image.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import me.m123.image.R
import me.m123.image.api.Requester
import me.m123.image.utils.ToolsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedbackActivity: BaseAAppCompatActivity() {

    private lateinit var FeedbackContent: EditText
    private lateinit var FeedbackEmail: EditText
    private lateinit var FeedbackSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.FeedbackContent = this.findViewById(R.id.feedback_content)
        this.FeedbackEmail = this.findViewById(R.id.feedback_email)
        this.FeedbackSubmit = this.findViewById(R.id.feedback_submit)

        this.FeedbackSubmit.setOnClickListener {
            val content = this.FeedbackContent.text.toString()
            val email = this.FeedbackEmail.text.toString()

            if (TextUtils.isEmpty(content) || TextUtils.isEmpty(email)) {
                Toast.makeText(this, "邮箱或反馈内容不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ToolsHelper.isEmailValid(email = email) != true) {
                Toast.makeText(this, "请输入一个正确的邮箱!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

        }

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_feedback
    }
}