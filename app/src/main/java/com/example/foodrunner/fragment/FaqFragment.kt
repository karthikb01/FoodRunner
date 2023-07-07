package com.example.foodrunner.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.R
import com.example.foodrunner.adapter.FaqAdapter
import com.example.foodrunner.model.Faq


class FaqFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var faqAdapter: FaqAdapter

    private val faq = arrayListOf(
        Faq(
            "Q1. What will be the timings of the training?\n" +
                    "\n",
            "As this is a purely online training program, you can choose to learn at any time of the day and for as much time as you want. We will recommend a pace to be followed throughout the program, but the actual timings and learning hours are in your hands."
        ),
        Faq(
            "Q2. What if I'm not able to complete my training in 6 weeks?\n" +
                    "\n",
            "Don't worry! If for some reason, you are not able to complete the training in 6 weeks; we would extend your access to the platform for sufficient duration (maximum upto another 6 weeks) so that you can complete it."
        ),
        Faq(
            "Q3. What are the benefits of 6 weeks access?\n" +
                    "\n",
            "Having 6 weeks access ensures you have a deadline to work with and are able to complete the training. It also helps you beat procrastination and maintain continuity in your studies which is why Internshala Trainings students have some of the best training completion rates in the industry."
        ),
        Faq(
            "Q4. When can I start the training?\n" +
                    "\n",
            "You can choose your preferred batch date while signing up for the training program and start accordingly."
        ),
        Faq(
            "Q5. What software/tools would be needed for the training and how can I get them?\n" +
                    "\n",
            "All the software/tools that you need for the training would be shared with you during the training as and when you need it."
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faq, container, false)
        recyclerView = view.findViewById(R.id.recyclerFaq)
        layoutManager = LinearLayoutManager(activity)

        recyclerView.layoutManager = layoutManager
        faqAdapter = FaqAdapter(activity as Context, faq)

        recyclerView.adapter = faqAdapter

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity as Context,
                layoutManager.orientation
            )
        )
        return view
    }


}