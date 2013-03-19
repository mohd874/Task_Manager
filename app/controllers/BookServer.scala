package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play

object BookServer extends Controller {

	val testPdf = "c:\\large.pdf"
	val uploadLoc = Play.current.configuration.getString("files.upload")

		def books = Action {
		Ok(views.html.books("Hello from Books Server"))
	}

	import play.api.libs.iteratee._
	def stream1 = Action {
		val file = new java.io.File(testPdf)
			val fileContent : Enumerator[Array[Byte]] = Enumerator.fromFile(file)

			SimpleResult(
				header = ResponseHeader(200),
				body = fileContent)
	}

	def stream2 = Action {
		val file = new java.io.File(testPdf)
			val fileContent : Enumerator[Array[Byte]] = Enumerator.fromFile(file)

			SimpleResult(
				header = ResponseHeader(200, Map(CONTENT_LENGTH->file.length.toString)),
				body = fileContent)
	}

	def stream3 = Action {
		Ok.sendFile(new java.io.File(testPdf), inline = true)
	}

	def getFile() = Action {
		Ok.sendFile(new java.io.File(testPdf), inline = true)
	}

	def pdfViewer = Action {
		Ok(views.html.flexPaper())
	}
	def uploadFileForm = Action {
		Ok(views.html.uploadFileForm(""))
	}
	def uploadDirect = Action(parse.temporaryFile) {
		request =>
			import java.io.File
			request.body.moveTo(new File(uploadLoc.get + request.body.file.getName))
			Ok(views.html.uploadFileForm("File Uploaded"))
	}
	def uploadFile = Action(parse.multipartFormData) {
		request =>
			request.body.file("pdf").map {
			pdf =>
				import java.io.File
				val filename = pdf.filename
				val contentType = pdf.contentType
				println("********************")
				println("filename: "+filename+" || contentType:"+contentType)
				println("********************")
				pdf.ref.moveTo(new File(uploadLoc.get+filename), true)
				Ok(views.html.uploadFileForm("File Uploaded"))
		}
		.getOrElse {
			Redirect(routes.Application.index).flashing(
				"error"->"Missing file")
		}
	}
}
