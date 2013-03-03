package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

object Application extends Controller {
  
  val taskForm = Form {
    "label" -> nonEmptyText
  }
  
  def index = Action {
    redirectToTasks
  }
  
  import models._
  
  def tasks = Action {
    Ok(views.html.index(Task.all(), taskForm))
  }
  
  def newTask = Action { implicit request =>
    taskForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index(Task.all(), errors)),
        label => {
            Task.create(label)
            redirectToTasks
        }
    )
  }
  
  def deleteTask(id: Long) = Action {
    Task delete id
    redirectToTasks
  }
  
  def redirectToTasks = Redirect(routes.Application.tasks)
} 