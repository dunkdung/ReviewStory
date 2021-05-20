package com.example.reviewstory.timeline

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.reviewstory.R
import com.example.reviewstory.REVIEW
import com.example.reviewstory.TIMELINE
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.list_item_stamp.view.*

/* ResultFragment에서 검색 결과를 리사이클러뷰에 데이터를 보여주는 어댑터  */
class ResultAdapter(val items: ArrayList<REVIEW>, val tlnum: TIMELINE, val fbFirestore: FirebaseFirestore) : RecyclerView.Adapter<ItemViewHolder>() {
    var mPosition = 0


    fun getPosition(): Int{
        return mPosition
    }

    fun setPosition(position: Int){
        mPosition = position
    }

    fun removeItem(position: Int, fbFirestore: FirebaseFirestore){
        if(position >= 0){
            items.removeAt(position)
            notifyDataSetChanged()
            if (items[position] != null) {
                fbFirestore.collection("stamp").document(items[position].d_id.toString()).delete()
                Log.d("delete", "삭제")
            }
        }
    }
    /* 뷰홀더를 생성하여 반환 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        //list_item_fresh 뷰 inflate
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_stamp, parent, false)
        return ItemViewHolder(rootView)
    }

    //뷰홀더에 데이터 바인딩(bindItems() 함수를 호출)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItems(items[position],tlnum, fbFirestore)

        holder.itemView.btn_del.setOnClickListener { view ->
            setPosition(position)
            removeItem(position, fbFirestore)
        }


    }

    //어댑터에서 관리할 아이템 갯수를 반환
    override fun getItemCount() = items.size
}

//뷰홀더 클래스 선언
class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindItems(stamp: REVIEW?, tlnum: TIMELINE?, fbFirestore: FirebaseFirestore) {

        val tlnum = tlnum
        var id: String? = null

        stamp?.let {
                if(stamp.write) itemView.setBackgroundColor(Color.GREEN)
            itemView.txt_gongpan_info.text = stamp.address.toString()
            itemView.txt_unit.text = stamp.s_name.toString()
            //itemView.txt_min_price.text = "메세지 수: "
            itemView.txt_avg_price.text = stamp.s_date.toString()
            //itemView.txt_max_price.text = "발신처: " + "${stamp.user_num}"

            itemView.setOnClickListener{
                //fbFirestore.collection("stamp").document("${stamp.s_num}").delete()
                val direction: NavDirections = StampsFragmentDirections.actionStampsFragmentToReviewFragment(
                    stamp.s_num.toString(),
                    tlnum?.tl_num.toString(),
                    tlnum?.start_date.toString(),
                    tlnum?.end_date.toString())
                findNavController(itemView).navigate(direction)

            }





        }


        }
    }
//end of ItemViewHolder
