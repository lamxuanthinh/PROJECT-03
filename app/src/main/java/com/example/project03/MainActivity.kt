package com.example.project03

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.project03.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


private lateinit var binding: ActivityMainBinding
private lateinit var dbRef: DatabaseReference

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Test connection database with Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("Employees")

        binding.btnAdd.setOnClickListener {
            saveEmployeesData()
        }
    }

    private fun saveEmployeesData() {
        val empId = dbRef.push().key!!
        val empName = binding.edtName.text.toString()
        val empAge = binding.edtAge.text.toString()
        val empDescription = binding.edtDescription.text.toString()

        binding.edtName.setText("")
        binding.edtAge.setText("")
        binding.edtDescription.setText("")

        val employee = EmployeeModel(empId, empName, empAge, empDescription)

        dbRef.child(empId).setValue(employee).addOnCompleteListener {
                Toast.makeText(this, "successfully connected", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { error ->
                Toast.makeText(this, "Fail connected ${error.message}", Toast.LENGTH_LONG).show()
            }
    }
}