package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.data.validation._
import scala.collection.mutable.HashMap
import com.typesafe.plugin._
import play.api.Play.current
import util.Random
import views._

import models._

object SignUp extends Controller {
  
  

  
//Login Form
  val loginForm: Form[UserLogIn] = Form(
    
    // Define a mapping that will handle User values
    mapping(
      
      "username" -> text(minLength = 4),
      "password" -> text(minLength = 4)
      )(UserLogIn.apply)(UserLogIn.unapply)

    )
//New User Form 
  val signupForm: Form[User] = Form(
    
    // Define a mapping that will handle User values
    mapping(
      "id" -> ignored(NotAssigned: Pk[Int]),
      "username" -> text(minLength = 4),
      "email" -> text(minLength = 4),
      "password" -> text(minLength = 4)
       
    )(User.apply)(User.unapply)
  )
  
   val confirmationForm: Form[String] =  Form(
      
	  "code" -> text
	
  )
  
//display form
  def form = Action {
    Ok(html.signup.form(signupForm,"Please enter a valid username, minimum of 4 characters."));
  } 
 //display login form
  def formLogin = Action {
    Ok(html.signup.login(loginForm));
  }
  
  def formConfirm(username: String, op: Int) = Action {
    val temp = User.returnUser(username)
    val code = email(temp)
    val strCode = ""+code

    Ok(html.signup.confirmationform(confirmationForm, strCode, username, op));
  }
// save a new user
  def save = Action { implicit request =>
    signupForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.signup.form(signupForm, enterAValidUserName)),
      user => {
        if(User.verifyUsername(user.username) == true)
        {
	        User.save(user)
	        Ok(html.signup.summary(user))
        }
        else
        {
          Ok(html.signup.form(signupForm, userNameAlreadyTaken))
        }
      })
  }
  def enterAValidUserName = "Please enter a valid username, minimum of 4 characters."
  def userNameAlreadyTaken = "The username you chose is already taken."
  
 //Edit a user
  def edit(username: String) = Action {
    val temp = User.returnUser(username)
    Ok(html.signup.editform(signupForm, temp.id.get, temp, enterAValidUserName))
  }
  def cancelEdit(id: Int) = Action
  {
    val tempuser = User.returnUserById(id)
    Ok(html.signup.summary(tempuser))
  }
  def update(id: Int) = Action
  {
    implicit request =>
    signupForm.bindFromRequest.fold(
     
      formWithErrors => 
      {
    	val temp = User.returnUserById(id)
        BadRequest(html.signup.editform(signupForm, id, temp, enterAValidUserName))
      },
      user => 
      {
        if(User.verifyEdit(user.username, id) == true)
        {
	        User.update(id, user)
	        Ok(html.signup.summary(user))
        }
        else
        {
          val temp = User.returnUserById(id)
          Ok(html.signup.editform(signupForm, id, temp, userNameAlreadyTaken))
        }
      })
  }
  //Delete a user by username
   def delete(username: String) = Action {
    User.delete(username)
    Ok(html.index())
  }
  //display all users in console
   def displayusers = Action {
    System.out.println("**************Users:**************")
    User.displayUsers
    System.out.println("**********************************")
    Ok(html.index())
  }
  
   def email(temp: User): Int = {    
   
    val code = Random.nextInt(99999)
	val mail = use[MailerPlugin].email
	mail.setSubject("Email Confirmation - Demo Project")
	mail.setRecipient(temp.email)
	mail.setFrom("projectdemosyndesi@gmail.com")
	mail.send( "Your confirmation code is: "+code )
	return code
	
	
   
  }
   
   //Login
  def submitLogin = Action { 
    implicit request =>
    loginForm.bindFromRequest.fold(
      // Form has errors, redisplay it
      errors => BadRequest(html.signup.login(loginForm)),
      // We got a valid User value, display the summary
      tempval => {
    	  		val temp = User.findBy(tempval.username, tempval.password, User(null, "","",""))
    	  		if(temp.username == tempval.username)
    	  		{
    	  		  if(temp.password == tempval.password)
    	  		  {
    	  			  Ok(html.signup.summary(temp))
    	  		  }
    	  		  else
    	  		  {
    	  		    Ok(html.signup.login(loginForm))
    	  		  }
    	  		}
    	  		else
    	  		{
    	  			Ok(html.signup.login(loginForm))
    	  		}
    	  		
      })
  }
  
  def deleteUser(id: String)
  {
    User.delete(id)
  }
   //Login
  def submitConfirmation(tempcode: String, id: String, op: Int)  = Action { 
    implicit request =>
    confirmationForm.bindFromRequest.fold(
      errors => BadRequest(html.index()),
      tempval => {
    	  		if(tempval == tempcode)
    	  		{
    	  		  val temp = User.returnUser(id)
    	  		  System.out.println("op = "+op)
    	  		  if(op == 1)
    	  		  {
    	  			  Ok(html.signup.editform(signupForm, temp.id.get, temp, enterAValidUserName))
    	  		  }
    	  		  else if(op == 2)
    	  		  {
    	  		   
    	  		    Ok(html.signup.deleteconfirmation(id))
    	  		  }
    	  		  else
    	  		  {
    	  		    Ok(html.index())
    	  		  }
    	  		}
    	  		else
    	  		{
    	  		  Ok(html.index())
    	  		}
    	  		
      })
  }

}