package com.ecs198f.foodtrucks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecs198f.foodtrucks.databinding.FragmentFoodTruckReviewsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse


const val RC_SIGN_IN = 123

class FoodTruckReviewsFragment(list: List<FoodReview>) : Fragment() {

    private lateinit var bindingReview: FragmentFoodTruckReviewsBinding
    private val viewModel: TruckIdViewModel by activityViewModels()

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

        lateinit var mGoogleSignInClient:GoogleSignInClient
        var account: GoogleSignInAccount? = null
        (requireActivity() as MainActivity).apply {
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            account = GoogleSignIn.getLastSignedInAccount(this)
            updateUI(account)

            bindingReview.BottonPostReview.setOnClickListener{
                account = GoogleSignIn.getLastSignedInAccount(this)
                Log.d("PostClickListener", "Clicked")
                if (account == null)
                    Log.d("No account", "Null")
                else
                    Log.d("Account in button", account!!.displayName!!)
                val truckId = viewModel.truckId.value!!
                val content = bindingReview.editTextReview.text.toString()
                val review = FoodReview("", truckId, "", "", content, emptyList())
                val token = "Bearer " + account!!.idToken
                foodTruckService.createFoodReviews(truckId, token, review).enqueue(object: Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        bindingReview.editTextReview.setText("")

                        Log.d("Review created", response.code().toString())
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {

                    }
                })
                viewModel.set(true)
            }
        }

        bindingReview.BottonSignIn.setOnClickListener{
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }



        return bindingReview.root

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        Log.d("onActivityResult", requestCode.toString())
        if (requestCode == RC_SIGN_IN) {
            Log.d("onActivityResult", "success")
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }


    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.d("handleSignIn", "handleSignIn")
        try {
            val account:GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            Log.d("Catch", e.statusCode.toString())
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