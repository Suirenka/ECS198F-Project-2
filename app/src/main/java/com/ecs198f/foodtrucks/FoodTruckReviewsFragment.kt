package com.ecs198f.foodtrucks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecs198f.foodtrucks.databinding.FragmentFoodTruckReviewsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


const val RC_SIGN_IN = 123

class FoodTruckReviewsFragment(list: List<FoodReview>) : Fragment() {



    private lateinit var bindingReview: FragmentFoodTruckReviewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingReview = FragmentFoodTruckReviewsBinding
            .inflate(inflater, container, false)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("699423245763-insnbbp034ep600msiqfan5g0b2pau67.apps.googleusercontent.com")
            .requestEmail()
            .build()

        (requireActivity() as MainActivity).apply {
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            fun signIn() {
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

            bindingReview.BottonSignIn.setOnClickListener{
                signIn()
                val account = GoogleSignIn.getLastSignedInAccount(this)
                Log.d("Sign In", "DONE")
                updateUI(account)
            }

        }



        return bindingReview.root
        //return inflater.inflate(R.layout.fragment_food_truck_reviews, container, false)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }


    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            bindingReview.BottonSignIn.visibility = View.VISIBLE
        }
    }

    val list = list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.foodReviewListRecyclerView)
        val recyclerViewAdapter = FoodReviewRecyclerViewAdapter(listOf())
        recyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(context)
        }
        recyclerViewAdapter.updateItems(list)

    }

    private fun updateUI(account:GoogleSignInAccount?) {
        Log.d("updateUI", if (account == null) "null" else account.displayName!!)
        if(account != null)
        {
            bindingReview.BottonSignIn.visibility = View.INVISIBLE
        }
        else
        {
            bindingReview.BottonSignIn.visibility = View.VISIBLE
        }
    }

    override fun onStart(){
        super.onStart()


        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        (requireActivity() as MainActivity).apply {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                updateUI(account)
            }

        }


    }
}