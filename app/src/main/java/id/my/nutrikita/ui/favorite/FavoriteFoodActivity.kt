package id.my.nutrikita.ui.favorite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.my.nutrikita.ViewModelFactory
import id.my.nutrikita.data.local.entity.FoodData
import id.my.nutrikita.databinding.ActivityFavoriteFoodBinding
import id.my.nutrikita.ui.customfood.CustomFoodActivity
import id.my.nutrikita.ui.detailfood.DetailFoodActivity

class FavoriteFoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteFoodBinding
    private lateinit var viewModel: FavoriteFoodViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = obtainViewModel(this)

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setRecyclerView()

        viewModel.getAllFavoriteFood().observe(this) {
            setupFavoriteFoodData(it)
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): FavoriteFoodViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[FavoriteFoodViewModel::class.java]
    }

    private fun setRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvFavoriteFood.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvFavoriteFood.setHasFixedSize(true)
        binding.rvFavoriteFood.addItemDecoration(itemDecoration)
    }

    private fun setupFavoriteFoodData(foods: List<FoodData>) {
        if (foods.isNotEmpty()) {
            binding.tvEmptyFavorite.visibility = View.GONE
            binding.ivKitten.visibility = View.GONE
            binding.rvFavoriteFood.visibility = View.VISIBLE
            binding.btnCheckFoodNutrition.visibility = View.GONE

            val adapter = FavoriteAdapter()
            adapter.submitList(foods)
            binding.rvFavoriteFood.adapter = adapter

            adapter.setOnItemClickCallback(object : FavoriteAdapter.OnItemClickCallback {
                override fun onItemClicked(data: FoodData) {
                    val intent = Intent(this@FavoriteFoodActivity, DetailFoodActivity::class.java)
                    intent.putExtra(DetailFoodActivity.EXTRA_FAV_FOOD_DATA, data)
                    startActivity(intent)
                }

                override fun onFavoriteClicked(data: FoodData, btnFavorite: ImageButton) {
                    data.name?.let { viewModel.deleteFavorite(it) }
                }

            })
        } else {
            binding.tvEmptyFavorite.visibility = View.VISIBLE
            binding.rvFavoriteFood.visibility = View.GONE
            binding.ivKitten.visibility = View.VISIBLE
            binding.btnCheckFoodNutrition.visibility = View.VISIBLE
            binding.btnCheckFoodNutrition.setOnClickListener {
                val intent = Intent(this, CustomFoodActivity::class.java)
                startActivity(intent)
            }
        }
    }
}