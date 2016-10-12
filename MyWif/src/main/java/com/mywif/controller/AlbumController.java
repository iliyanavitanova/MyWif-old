package com.mywif.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.mywif.model.db.PostDAO;
import com.mywif.model.pojo.Post;
import com.mywif.model.pojo.UsersManager;

import io.undertow.server.session.Session;

import com.mywif.model.db.AlbumDAO;

@Controller
@SessionAttributes({ "animalsPosts", "abstractPosts", "foodPosts", "peoplePosts", "naturePosts", "urbanPosts",
		"uncategorizedPosts", "familyPosts", "sportPosts", "travelPosts" })
public class AlbumController {

	@RequestMapping(value = "/createalbum", method = RequestMethod.POST)
	public String createAlbum(@RequestParam(value = "name") String name, HttpSession session,
			HttpServletRequest request) {
		String email = session.getAttribute("USER").toString();
		AlbumDAO.getInstance().addAlbum(name, email, Timestamp.valueOf(LocalDateTime.now()), new ArrayList<>());
		return "myAlbums";
	}

	@RequestMapping(value = "/createpost", method = RequestMethod.POST)
	public String createPost(@RequestParam(value = "category") String category,
			@RequestParam(value = "nameOfPost") String nameOfPost, @RequestParam(value = "keyWords") String keyWords,
			@RequestParam(value = "email") String email, @RequestParam("fileField") MultipartFile picture,
			@RequestParam("albumId") int albumId, Model model, HttpServletRequest request) throws IOException {
		category = category.toLowerCase();
		InputStream pictureStream = picture.getInputStream();
		File dir = new File("D:\\MyWifPictures\\userPostPics" + UsersManager.getInstance().getUser(email).getName());
		if (!dir.exists()) {
			dir.mkdir();
		}
		File pictureFile = new File(dir,
				UsersManager.getInstance().getUser(email).getName() + LocalDateTime.now().toString().replaceAll(":", "")
						+ "-post-pic." + picture.getContentType().split("/")[1]);
		Files.copy(pictureStream, pictureFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		PostDAO.getInstance().addPost(email, albumId, category, pictureFile.getName(), nameOfPost, keyWords,
				Timestamp.valueOf(LocalDateTime.now()), new ArrayList<>(), new HashSet<>(), new HashSet<>());
		model.addAttribute(category + "Posts", PostDAO.getInstance().getAllPostsByCategory(category).size());
		return "main";
	}

	@RequestMapping(value = "/postlike", method = RequestMethod.POST)
	public String likePost(@RequestParam(value = "postId") String postId, Model model, HttpSession session) {
		System.out.println(postId);
		Post post = PostDAO.getInstance().getPost(Integer.parseInt(postId));
		model.addAttribute("postId", postId);
		model.addAttribute("post", post);
		String email = session.getAttribute("USER").toString();
		PostDAO.getInstance().likePost(Integer.parseInt(postId), email);
		return "detailsPost";
	}

	@RequestMapping(value = "/postdislike", method = RequestMethod.POST)
	public String dislikePost(@RequestParam(value = "postId") String postId, Model model, HttpSession session) {
		Post post = PostDAO.getInstance().getPost(Integer.parseInt(postId));
		model.addAttribute("postId", postId);
		model.addAttribute("post", post);
		String email = session.getAttribute("USER").toString();
		PostDAO.getInstance().dislikePost(Integer.parseInt(postId), email);
		System.out.println("disliknah te be da ti eba maikata");
		return "detailsPost";
	}

}
