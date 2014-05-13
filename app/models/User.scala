package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class User(
  id: Pk[Int] = NotAssigned,
  username: String, 
  email: String,
  password: String
 
)

case class UserLogIn
(
	username: String,
	password: String
)

object User 
{ 
 //Saves a new User into the DB
  def save(user: User)
  {
    DB.withConnection { implicit connection =>
      SQL("""
            INSERT INTO User(username, email, password) 
            VALUES({username}, {email}, {password})
      """).on(
          'username -> user.username,
          'email -> user.email,
          'password -> user.password
      ).executeUpdate
    }
    
  }
 //Used for list
  private val userParser: RowParser[User] = {
	  get[Pk[Int]]("id") ~
      get[String]("username") ~
      get[String]("email") ~
      get[String]("password") map {
      case id ~ username ~ email ~ password => User(id, username, email, password)
    }}
 //Used for listUser
  val parsedValueOfUser = {
	  get[Pk[Int]]("id") ~
      get[String]("username") ~
      get[String]("email") ~
      get[String]("password") map {
      case id ~ username ~ email ~ password => User(id, username, email, password)
	 }}
//Returns a list of all users in list[Rowparser[User]]
  def list = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * from User").as(userParser *)
    }
  }
//Returns a list of all the users in list[User] 
  def listUser = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * from User").as(parsedValueOfUser *)
    }
  }
//Under construction
  def update(id: Int, tempu: User) {
    System.out.println("New Values for user:")
    System.out.println("\tid: "+id)
    System.out.println("\tusername: "+tempu.username)
    System.out.println("\temail: "+tempu.email)
    System.out.println("\tpassword: "+tempu.password)
 DB.withConnection { implicit connection =>
      SQL(""" 
            UPDATE User SET
            username = {username},
            email = {email},
            password = {password}
            WHERE id = {id}
      """).on(
          'id -> id,
          'username -> tempu.username,
          'email -> tempu.email,
          'password -> tempu.password
      ).executeUpdate
    }
  }
//Deletes The user specified by username
  def delete(username: String) {
    DB.withConnection { implicit connection =>
      SQL(""" 
          DELETE FROM User where username = {username}
      """).on(
          'username -> username
      ).executeUpdate
    }
    System.out.println("Deleted!")
  }
//Displays the users in the console for testing purposes
  def displayUsers = {
   val templist = listUser
   for(i <- 0 until templist.length)
   {
     System.out.println("-User "+i)
	 System.out.println("\tUser id: "+ templist(i).id.get)
	 System.out.println("\tUsername: "+ templist(i).username)
	 System.out.println("\tEmail: "+ templist(i).email)
	 System.out.println("\tPassword: "+ templist(i).password)
   }
  }
//Used to log the user in, compares username and password to all Users
  def findBy(usern: String, passw: String, temp: User): User = {
   val templist = listUser
   for(i <- 0 until templist.length)
   {
	 if(templist(i).username == usern)
	 {
	   if(templist(i).password == passw )
	   {
	       return templist(i)
	   }
	 }
   }
   return temp
  }
//Used to Confirm There are not 2 of the same usernames being used
  def verifyUsername(usern: String): Boolean = 
 {
   val templist = listUser
   for(i <- 0 until templist.length)
   {
	 if(templist(i).username == usern)
	 {
	   return false
	 }
   }
  return true
}
 //return id associated to this username
 def returnId(usern: String): Int = 
 {
   val templist = listUser
  
   for(i <- 0 until templist.length)
   {
	 if(templist(i).username == usern)
	 {
	   val temp = templist(i).id.get
	   System.out.println("id = "+temp)
	   return temp
	 }
   }
  return -1
}
 def returnUser(usern: String): User = 
 {
   val templist = listUser
  
   for(i <- 0 until templist.length)
   {
	 if(templist(i).username == usern)
	 {
	   val temp = templist(i)
	   return temp
	 }
   }
  return null
}
 def returnUserById(id: Int): User = 
 {
   val templist = listUser
  
   for(i <- 0 until templist.length)
   {
	 if(templist(i).id.get == id)
	 {
	   val temp = templist(i)
	   return temp
	 }
   }
  return null
}
  def returnId2(usern: String): Pk[Int] = 
 {
   val templist = listUser
  
   for(i <- 0 until templist.length)
   {
	 if(templist(i).username == usern)
	 {
	   val temp = templist(i).id
	   System.out.println("id = "+temp)
	   return temp
	 }
   }
   return null
}
 def verifyEdit(usern: String, id: Int): Boolean = 
 {
   val templist = listUser
   for(i <- 0 until templist.length)
   {
	 if(templist(i).username == usern)
	 {
	   if(templist(i).id.get != id)
	   {
		  return false
	   }
	 }
   }
  return true
}
}