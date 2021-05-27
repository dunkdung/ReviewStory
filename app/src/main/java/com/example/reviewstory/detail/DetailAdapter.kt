package com.example.reviewstory.detail


import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.example.reviewstory.MyItem
import com.example.reviewstory.mypage.TimeFragmentDirections
import com.transferwise.sequencelayout.SequenceAdapter
import com.transferwise.sequencelayout.SequenceStep



class MyAdapter(private val items: List<MyItem>) : SequenceAdapter<MyItem>() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): MyItem {
        return items[position]
    }

    override fun bindView(sequenceStep: SequenceStep, item: MyItem) {
        with(sequenceStep) {
            setActive(item.isActive)
            setAnchor(item.formattedDate)
            //setAnchorTextAppearance(...)
            setTitle(item.title)
            setOnClickListener {
                val direction: NavDirections = DetailFragmentDirections.actionDetailFragmentSelf(item.d_id.toString(),item.tl_num.toString())
                Navigation.findNavController(this).navigate(direction)
            }
            //setTitleTextAppearance()
            //setSubtitle(...)
            //setSubtitleTextAppearance(...)
        }
    }
}
class MyAdapter2(private val items: List<MyItem>) : SequenceAdapter<MyItem>() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): MyItem {
        return items[position]
    }

    override fun bindView(sequenceStep: SequenceStep, item: MyItem) {
        with(sequenceStep) {
            setActive(item.isActive)
            setAnchor(item.formattedDate)
            //setAnchorTextAppearance(...)
            setTitle(item.title)
            setOnClickListener {
                val direction: NavDirections = TimeFragmentDirections.actionTimeFragmentToDetailFragment(item.d_id.toString(),item.tl_num.toString())
                Navigation.findNavController(this).navigate(direction)
            }
            //setTitleTextAppearance()
            //setSubtitle(...)
            //setSubtitleTextAppearance(...)
        }
    }
}
