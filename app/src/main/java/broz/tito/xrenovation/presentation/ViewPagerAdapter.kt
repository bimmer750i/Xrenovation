package broz.tito.xrenovation.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(fm: FragmentManager,lifecycle : Lifecycle) : FragmentStateAdapter(fm,lifecycle) {

    val fragments: ArrayList<Fragment> = ArrayList()
    val fragmentTitle: ArrayList<String> = ArrayList()

    fun add(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentTitle.add(title)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments.get(position)
    }


}