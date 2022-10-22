package edu.northeastern.numad22fateam26;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.northeastern.numad22fateam26.model.Recipe;

public class RecyclerViewRecipeAdapter extends RecyclerView.Adapter<RecyclerViewRecipeAdapter.RecyclerViewHolder> {

    private List<Recipe> recipes;
    private Context context;
    private static ClickListener clickListener;

    public RecyclerViewRecipeAdapter(List<Recipe> recipes, Context context) {
        this.recipes = recipes;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewRecipeAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_recipe,
                viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewRecipeAdapter.RecyclerViewHolder viewHolder, int i) {

        String imageLink = recipes.get(i).getImage();
        Picasso.get().load(imageLink).placeholder(R.drawable.ic_circle).into(viewHolder.recipeImage);

        String title = recipes.get(i).getTitle();
        viewHolder.recipeTitle.setText(title);
    }


    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipeImage)
        ImageView recipeImage;
        @BindView(R.id.recipeTitle)
        TextView recipeTitle;
        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }


    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerViewRecipeAdapter.clickListener = clickListener;
    }


    public interface ClickListener {
        void onClick(View view, int position);
    }
}