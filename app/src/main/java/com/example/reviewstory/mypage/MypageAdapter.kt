package com.example.reviewstory.mypage

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.reviewstory.R
import com.example.reviewstory.REVIEW
import com.example.reviewstory.TIMELINE
import com.example.reviewstory.search.SearchFragmentDirections
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.list_item_stamp.view.*
import kotlinx.android.synthetic.main.list_item_timeline.view.*
import kotlinx.android.synthetic.main.list_item_timeline.view.txt_gongpan_info
import kotlinx.android.synthetic.main.list_item_timeline.view.txt_unit

class MypageAdapter(val items: ArrayList<REVIEW>, val fbFirestore: FirebaseFirestore) : RecyclerView.Adapter<ItemViewHolder>() {

    /* 뷰홀더를 생성하여 반환 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        //list_item_fresh 뷰 inflate
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_timeline, parent, false)
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
    fun bindItems(stamp: REVIEW, fbFirestore: FirebaseFirestore) {
        stamp?.let {
            if(stamp.write) itemView.setBackgroundColor(Color.GREEN)
            itemView.txt_gongpan_info.text = stamp.address.toString()
            itemView.txt_unit.text = stamp.s_name.toString()
            //itemView.txt_min_price.text = "메세지 수: "
            itemView.txt_avg_price.text = stamp.s_date.toString()
            //itemView.txt_min_price.text = "메세지 수: "
            //itemView.txt_avg_price.text =
                //"방문날짜: " + "${stamp.s_date}"
            //itemView.txt_max_price.text = "발신처: " + "${stamp.user_num}"
            /*itemView.setOnClickListener{
                val direction: NavDirections = SearchFragmentDirections.actionSearchFragmentToDetailFragment2(stamp.d_id.toString(),stamp.tl_num.toString())
                Navigation.findNavController(itemView).navigate(direction)
            }*/
        }
    }
}