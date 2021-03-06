package com.example.reviewstory.mypage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.reviewstory.FOLLOWLIST
import com.example.reviewstory.MyItem
import com.example.reviewstory.R
import com.example.reviewstory.TIMELINE
import com.example.reviewstory.detail.MyAdapter2
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.list_item_review.view.txt_start
import kotlinx.android.synthetic.main.list_item_review.view.tv_review
import kotlinx.android.synthetic.main.list_item_timeline.view.*

class MypageAdapter(val items: ArrayList<TIMELINE>, val fbFirestore: FirebaseFirestore) : RecyclerView.Adapter<ItemViewHolder>() {

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
class FollowAdapter(val items: ArrayList<FOLLOWLIST>, val fbFirestore: FirebaseFirestore) : RecyclerView.Adapter<ItemViewHolder2>() {

    /* 뷰홀더를 생성하여 반환 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder2 {
        //list_item_fresh 뷰 inflate
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_detail, parent, false)
        return ItemViewHolder2(rootView)
    }

    //뷰홀더에 데이터 바인딩(bindItems() 함수를 호출)
    override fun onBindViewHolder(holder: ItemViewHolder2, position: Int) {
        holder.bindItems(items[position],fbFirestore)

    }

    //어댑터에서 관리할 아이템 갯수를 반환
    override fun getItemCount() = items.size
}
class ItemViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindItems(stamp: FOLLOWLIST, fbFirestore: FirebaseFirestore) {
        var date = "2021-04-30"
        var items = ArrayList<FOLLOWLIST>()
        stamp?.let {
            itemView.txt_start.text = stamp.follow_id
        }
        itemView.setOnClickListener {
            val direction: NavDirections = FollowFragmentDirections.actionFollowFragmentToTimeFragment(date,stamp.fol_num.toString())
            Navigation.findNavController(itemView).navigate(direction)
        }
    }
}

//뷰홀더 클래스 선언
class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindItems(stamp: TIMELINE, fbFirestore: FirebaseFirestore) {

        itemView.folding_cell.setOnClickListener {
                itemView.folding_cell.toggle(false)
        }
        itemView.textView15.setOnClickListener {
            itemView.folding_cell.toggle(false)
        }
        var items = ArrayList<MyItem>()
        stamp?.let {
            itemView.tv_review.text = stamp.tl_date.toString()
            itemView.txt_start.text = stamp.start_date.toString()
            itemView.txt_end.text = stamp.end_date
            itemView.textView15.text = stamp.start_date.toString() + " ~ "+ stamp.end_date.toString()
            fbFirestore.collection("timeline").document(stamp.tl_num.toString()).collection("review").orderBy("s_date").get().addOnSuccessListener { result ->
                for (document in result) {
                    var item =  MyItem()
                    var a = IntRange(5, 9) // 0, 1, 2, 3 포함
                    var date1 = (document.data["s_date"] as String?)?.slice(a)
                    item.isActive = false
                    item.formattedDate = date1.toString()
                    item.title = document.data["s_name"] as String
                    item.tl_num = document.data["tl_num"] as String?
                    item.d_id = document.data["d_id"] as String?
                    items.add(item)
            }
                Log.d("place", items.toString())
                if(items.toString() != "[]") {
                    itemView.sequneceLayout2.setAdapter(MyAdapter2(items))
                }
            }
        }

    }
}