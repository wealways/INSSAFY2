package com.ssafy.pjt1.controller;

import java.util.HashMap;
import java.util.Map;

import com.ssafy.pjt1.model.dto.comment.CommentDto;
import com.ssafy.pjt1.model.service.BoardService;
import com.ssafy.pjt1.model.service.comment.CommentService;
import com.ssafy.pjt1.model.service.post.PostService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = { "*" }, maxAge = 6000)
@RestController
@RequestMapping("/comment")
public class CommentController {

    public static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private static final String PERMISSION = "No Permission";

    @Autowired
    private CommentService commentService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private PostService postService;

    /*
     * 기능: 댓글 생성
     * 
     * developer: 윤수민
     * 
     * @param : user_id, post_id, comment_description
     * 
     * @return : message
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> commentCreate(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.ACCEPTED;
        logger.info("comment/create 호출성공");
        try {
            String user_id = (String) param.get("user_id");
            int post_id = (int) param.get("post_id");
            int board_id = boardService.getIdbyPostId(post_id);
            Map<String, Object> map = new HashMap<>();
            map.put("user_id", user_id);
            map.put("board_id", board_id);
            if(boardService.isUnSubscribed(map)!=0){
                CommentDto commentDto = new CommentDto();
                commentDto.setUser_id(user_id);
                commentDto.setPost_id(post_id);
                commentDto.setComment_description((String) param.get("comment_description"));
                commentService.createComment(commentDto);

                int comment_id = commentDto.getComment_id();
                commentService.createNotification(comment_id);

                resultMap.put("message", SUCCESS);
            }else{
                resultMap.put("message", PERMISSION);
            }
            
        } catch (Exception e) {
            logger.error("실패", e);
            resultMap.put("message", FAIL);
        }
        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

    /*
     * 기능: 댓글 수정
     * 
     * developer: 윤수민
     * 
     * @param : CommentDto, login_id
     * 
     * @return : message
     */
    @PutMapping("/modify")
    public ResponseEntity<Map<String, Object>> commentModify(@RequestBody CommentDto commentDto,
    @RequestParam(value = "login_id") String login_id) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.ACCEPTED;
        logger.info("comment/modify 호출 성공");
        try {
            if(login_id.equals(commentDto.getUser_id())){
                if (commentService.commentModify(commentDto) == 1) {
                    resultMap.put("message", SUCCESS);
                }
            }else{
                resultMap.put("message", PERMISSION);
            }
            
        } catch (Exception e) {
            resultMap.put("message", FAIL);
            logger.error("error", e);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

    /*
     * 기능: 댓글 삭제
     * 
     * developer: 윤수민
     * 
     * @param : comment_id
     * 
     * @return : message
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> commentDelete(@RequestParam(value = "comment_id") int comment_id,
    @RequestParam(value = "login_id") String login_id) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.ACCEPTED;
        logger.info("comment/delete 호출성공");
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("login_id", login_id);
            map.put("comment_id", comment_id);
            if(commentService.isCommentWriter(map)!=0){
                if (commentService.commentDelete(comment_id) == 1) {
                    commentService.notificationDelete(comment_id);
                    resultMap.put("message", SUCCESS);
                }
            }else{
                resultMap.put("message", PERMISSION);
            }
            
        } catch (Exception e) {
            resultMap.put("message", FAIL);
            logger.error("error", e);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

}
