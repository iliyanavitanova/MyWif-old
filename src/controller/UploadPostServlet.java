package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import model.db.IPostDAO;
import model.db.PostDAO;
import model.pojo.Post;
import model.pojo.UsersManager;

@WebServlet("/UploadPost")
public class UploadPostServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email=(String) request.getSession().getAttribute("USER");
		String tag=request.getParameter("tag");
		Part picture = request.getPart("file");
		InputStream pictureStream = picture.getInputStream();
		if(tag.equalsIgnoreCase("people")||tag.equalsIgnoreCase("fun")||tag.equalsIgnoreCase("pets")||tag.equalsIgnoreCase("nature")||tag.equalsIgnoreCase("food and drinks")){
			File dir = new File(UsersManager.getInstance().getUser(email).getName()+"userPostPics");
			if (!dir.exists()) {
				dir.mkdir();
			}
			File pictureFile=new File(dir, UsersManager.getInstance().getUser(email).getName()+UsersManager.getInstance().getUser(email).getPosts().size() + "-post-pic." + picture.getContentType().split("/")[1]);
			if(!pictureFile.exists()){
				pictureFile.createNewFile();
			}			
			Files.copy(pictureStream, pictureFile.toPath(),StandardCopyOption.REPLACE_EXISTING );
			PostDAO.getInstance().addPost(email, tag, pictureFile.getName(), 0, 0, Timestamp.valueOf(LocalDateTime.now()), new ArrayList<>());
		}
	}

}
