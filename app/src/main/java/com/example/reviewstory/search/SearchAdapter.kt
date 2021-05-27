package com.example.reviewstory.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reviewstory.R
import com.example.reviewstory.REVIEW
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.list_item_review.view.*

/* ResultFragment에서 검색 결과를 리사이클러뷰에 데이터를 보여주는 어댑터  */
class SearchAdapter(val items: ArrayList<REVIEW>, val fbFirestore: FirebaseFirestore) : RecyclerView.Adapter<ItemViewHolder>() {

    /* 뷰홀더를 생성하여 반환 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        //list_item_fresh 뷰 inflate
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_review, parent, false)
        return ItemViewHolder(rootView)
    }

    //뷰홀더에 데이터 바인딩(bindItems() 함수를 호출)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItems(items[position],fbFirestore)
    }

    //어댑터에서 관리할 아이템 갯수를 반환
    override fun getItemCount() = items.size
}

//뷰홀더 클래스 선언
class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindItems(stamp: REVIEW?, fbFirestore: FirebaseFirestore) {

        stamp?.let {
            itemView.txt_tldate.text = stamp.address.toString()
            itemView.txt_start.text = stamp.s_name.toString()
            itemView.rating_point.text = stamp.score.toString()
            stamp.score?.toFloat()?.let { it1 -> itemView.appCompatRatingBar.setRating(it1) }
            itemView.text_review.text = stamp.rv_txt.toString()
            itemView.ed_writer.text = stamp.user_nick.toString()
            //Log.d("마지막5", stamp.user_nick.toString())

//            if(itemView.ed_writer.text == null){
//                itemView.ed_writer.text = stamp.g_nick.toString()
//            }
//            itemView.appCompatImageView.setImageResource(stamp.rv_img.)

             Glide.with(itemView)
                    .load(stamp.rv_img)
                    .override(600,200)
                    .into(itemView.appCompatImageView)


            //itemView.txt_min_price.text = "메세지 수: "
//            itemView.txt_avg_price.text =
//                "방문날짜: " + "${stamp.s_date}"
            //itemView.txt_max_price.text = "발신처: " + "${stamp.user_num}"
            itemView.setOnClickListener{
                val direction: NavDirections = SearchFragmentDirections.actionSearchFragmentToDetailFragment2(stamp.d_id.toString(),stamp.tl_num.toString())
                findNavController(itemView).navigate(direction)
            }
        }
    }
}//end of ItemViewHolder
