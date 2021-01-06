package com.example.inhabus

public class User {
    private lateinit var userNickname: String
    private lateinit var userEmail: String
    private lateinit var userPasswd: String

    public fun getUserNickname(): String {
        return userNickname
    }

    public fun getUserEmail(): String{
        return userEmail
    }

    public fun getUserPasswd(): String{
        return userPasswd
    }

    public fun setUserNickname(userNickname: String){
        this.userNickname = userNickname
    }

    public fun setUserEmail(userEmail: String){
        this.userEmail = userEmail
    }

    public fun setUserPasswd(userPasswd: String){
        this.userPasswd = userPasswd
    }
}