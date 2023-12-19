package id.my.nutrikita.ui.checkfoodresult

import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import id.my.nutrikita.BuildConfig
import id.my.nutrikita.R
import id.my.nutrikita.data.remote.response.Data
import id.my.nutrikita.databinding.ActivityCheckFoodResultBinding
import java.util.Locale

class CheckFoodResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckFoodResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckFoodResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnArrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setRecyclerView()
        setResult()
    }

    private fun setRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvNutritionalContent.layoutManager = layoutManager
    }

    private fun setResult() {
        val result = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_CHECK_FOOD_RESULT, Data::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CHECK_FOOD_RESULT)
        }

        binding.tvFoodName.text = result?.prediction
        binding.tvTriviaContent.text = result?.description
        Glide.with(this)
            .load(BuildConfig.ML_URL + result?.imageUrl)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(binding.ivFoodResult)

        val adapter = NutritionsAdapter()
        adapter.submitList(result?.nutritions)
        binding.rvNutritionalContent.adapter = adapter

        val percentages = result?.percentages

        val headerRow = TableRow(this)
        headerRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        if (percentages != null) {
            for (percentage in percentages) {
                val dataRow = TableRow(this)
                dataRow.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )

                val cells = if (percentage.lowercase(Locale.ROOT) == "vitamin") {
                    percentage.split(" ", limit = 3)
                } else {
                    percentage.split(" ", limit = 2)
                }

                for (cellData in cells) {
                    val dataCell = TextView(this)
                    dataCell.text = cellData
                    dataCell.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                    dataCell.setPadding(16, 8, 16, 8)
                    val customFont: Typeface? =
                        ResourcesCompat.getFont(this, R.font.poppins_regular)
                    dataCell.typeface = customFont

                    dataRow.addView(dataCell)
                }

                binding.tableLayoutPercentages.addView(dataRow)
            }
        }

    }

    companion object {
        const val EXTRA_CHECK_FOOD_RESULT = "extra_result"
    }
}