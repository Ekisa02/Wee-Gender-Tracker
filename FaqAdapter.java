package com.Joseph.WEE_GEnder_Tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {

    private List<FaqItem> faqList;
    private List<FaqItem> faqListFull; // for filtering
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDelete(FaqItem item);
    }

    public FaqAdapter(List<FaqItem> faqList, OnDeleteClickListener listener) {
        this.faqList = new ArrayList<>(faqList);
        this.faqListFull = new ArrayList<>(faqList);
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_item, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        FaqItem item = faqList.get(position);
        holder.textViewQuestion.setText(item.getQuestion());
        holder.textViewAnswer.setText(item.getAnswer());

        // Expand/Collapse logic
        boolean isExpanded = holder.textViewAnswer.getVisibility() == View.VISIBLE;
        holder.textViewAnswer.setVisibility(isExpanded ? View.GONE : View.VISIBLE);

        // Reset visibility each bind to prevent wrong state after recycling
        holder.textViewAnswer.setVisibility(View.GONE);

        holder.textViewQuestion.setOnClickListener(v -> {
            if (holder.textViewAnswer.getVisibility() == View.GONE) {
                // Animate expand
                holder.textViewAnswer.setVisibility(View.VISIBLE);
                holder.textViewAnswer.setAlpha(0f);
                holder.textViewAnswer.animate().alpha(1f).setDuration(300).start();
            } else {
                // Animate collapse
                holder.textViewAnswer.animate().alpha(0f).setDuration(300).withEndAction(() -> holder.textViewAnswer.setVisibility(View.GONE)).start();
            }
        });

        // Delete button
        holder.buttonDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDelete(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public void filterList(List<FaqItem> filteredList) {
        faqList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }

    public List<FaqItem> getFaqList() {
        return faqList;
    }

    static class FaqViewHolder extends RecyclerView.ViewHolder {
        TextView textViewQuestion, textViewAnswer;
        ImageButton buttonDelete;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.faq_question);
            textViewAnswer = itemView.findViewById(R.id.faq_answer);
            buttonDelete = itemView.findViewById(R.id.delete_button);
        }
    }
}
