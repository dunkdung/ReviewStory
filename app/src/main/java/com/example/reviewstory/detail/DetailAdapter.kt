package com.example.reviewstory.detail


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reviewstory.R
import com.example.reviewstory.REVIEW
import com.example.reviewstory.timeline.StampsFragmentDirections
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.list_item_detail.view.*



/* ResultFragment에서 검색 결과를 리사이클러뷰에 데이터를 보여주는 어댑터  */
class DetailAdapter(val items: ArrayList<REVIEW>, val fbFirestore: FirebaseFirestore) : RecyclerView.Adapter<ItemViewHolder>() {

    /* 뷰홀더를 생성하여 반환 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        //list_item_fresh 뷰 inflate
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_detail, parent, false)
        return ItemViewHolder(rootView)
    }
    //뷰홀더에 데이터 바인딩(bindItems() 함수를 호출)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItems(items[position], fbFirestore)
        val layoutParams = holder.itemView.layoutParams
        layoutParams.width = 1000
        layoutParams.height = 1000
        holder.itemView.requestLayout()
    }

    //어댑터에서 관리할 아이템 갯수를 반환
    override fun getItemCount() = items.size
}
//뷰홀더 클래스 선언
class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindItems(stamp: REVIEW, fbFirestore: FirebaseFirestore) {
        Log.d("place",stamp.rv_txt.toString())
        itemView.txt_unit.text = stamp.rv_txt
        Glide.with(itemView)
            .load(stamp.rv_img)
            .override(600,200)
            .into(itemView.imageView4)
        itemView.setOnClickListener{
            val direction: NavDirections = DetailFragmentDirections.actionDetailFragmentSelf(stamp.d_id.toString(),stamp.tl_num.toString())
            Navigation.findNavController(itemView).navigate(direction)
            }
        }
}