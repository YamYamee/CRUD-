import React, { useEffect, useState } from 'react';
import NavBar from './NavBar';
import styled from 'styled-components';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const Container = styled.div`
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
`;

const Title = styled.h1`
    font-size: 2rem;
    color: #333;
    margin-bottom: 20px;
`;

const MetaInfo = styled.div`
    margin-bottom: 10px;
`;

const MetaItem = styled.span`
    margin-right: 10px;
    font-size: 0.9rem;
`;

const Content = styled.div`
    margin-top: 20px;
    line-height: 1.6;
`;

const Image = styled.img`
    margin-top: 20px;
    line-height: 1.6;
`;

const CommentContainer = styled.div`
    margin-top: 20px;
`;

const Comment = styled.div`
    background-color: #f5f5f5;
    padding: 10px;
    margin-bottom: 10px;
`;

const CommentInfo = styled.div`
    display: flex;
    justify-content: space-between;
`;

const CommentActions = styled.div`
    margin-top: 5px;
`;

const CommentAuthor = styled.div`
    font-weight: bold;
`;

const CommentContent = styled.div`
    margin-top: 5px;
`;

const CommentForm = styled.form`
    margin-top: 20px;
`;


const CommentInput = styled.textarea`
    width: 100%;
    padding: 10px;
    font-size: 1rem;
    border: none;
    border-bottom: 2px solid #3f51b5;
    outline: none;
    resize: vertical;
    margin-bottom: 10px;
`;

const SubmitButton = styled.button`
    background-color: #3f51b5;
    color: white;
    border: none;
    padding: 5px 10px;
    cursor: pointer;
    font-size: 0.8rem;
    border-radius: 3px;
    transition: background-color 0.3s ease;

    &:hover {
        background-color: #303f9f;
    }
`;

const ActionButton = styled.button`
    background-color: #f44336;
    color: white;
    border: none;
    padding: 5px 10px;
    cursor: pointer;
    font-size: 0.8rem;
    border-radius: 3px;
    margin-left: 5px;
    transition: background-color 0.3s ease;

    &:hover {
        background-color: #d32f2f;
    }
`;


const PostDetail = () => {
    let { id } = useParams();
    const token = sessionStorage.getItem("accessToken");
    let [post, setPost] = useState("");
    const [comments, setComments] = useState([]);
    const [editMode, setEditMode] = useState({
        active: false,
        id: null,
        content: ''
    });
    const [newComment, setNewComment] = useState('');

    let navigate = useNavigate();

    useEffect(() => {
        const fetchData = async (id) => {
            try {
                const response = await axios.get(`http://15.165.102.113:8080/api/users/posts/${id}`);
                console.log(response.data.result);
                setPost(response.data.result);
                setComments(response.data.result.comments);
            } catch (err) {
                alert("데이터 불러오기 실패");
            }
        };
        fetchData(id);
    }, [id]);

    const handleNewCommentChange = (event) => {
        setNewComment(event.target.value);
    };

    const handleNewCommentSubmit = async (event) => {
        event.preventDefault();
        if (newComment.trim() !== '') {
            const userData = {
                content: newComment,
                postId: id
            };
            try {
                const response = await fetch('http://15.165.102.113:8080/api/users/comments', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(userData),
                });
                if (response.ok) {
                    alert('댓글이 성공적으로 작성되었습니다.');
                    window.location.reload();
                } else {
                    alert('댓글 작성 실패');
                }
            } catch (error) {
                alert('댓글 작성 중 오류가 발생했습니다.');
            }
        }
    };

    const handleEditClick = (id, content) => {
        setEditMode({
            active: true,
            id,
            content
        });
    };

    const handleEditChange = (event) => {
        setEditMode({
            ...editMode,
            content: event.target.value
        });
    };

    const handleEditSubmit = async (event) => {
        event.preventDefault();
        const updatedComment = {
            postId: editMode.id,
            content: editMode.content
        };
        try {
            const response = await fetch(`http://15.165.102.113:8080/api/users/edits/comment/${editMode.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(updatedComment),
            });
            if (response.ok) {
                alert('댓글이 성공적으로 수정되었습니다.');
                window.location.reload();
            } else {
                alert('댓글 수정 실패');
            }

        } catch (error) {
            alert('댓글 수정 중 오류가 발생했습니다.');
        }
    };

    const handleDelete = async (id) => {

        try {
            const response = await fetch(`http://15.165.102.113:8080/api/users/comment/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (response.ok) {
                alert('삭제 성공');
                window.location.reload();
            } else {
                alert('댓글 삭제 실패');
            }
        } catch (error) {
            alert('댓글 삭제 중 오류가 발생했습니다.');
        }
    };

    return (
        <div>
            <NavBar />
            <Container>
                <Title>{post.postTitle}</Title>
                <MetaInfo>
                    <MetaItem>작성자: {post.postWriter}</MetaItem>
                    <MetaItem>작성일: {post.createdAt}</MetaItem>
                </MetaInfo>
                <Content>{post.postContent}</Content>
                <Image src = {post.imageURL}/>
                <CommentForm onSubmit={handleNewCommentSubmit}>
                    <CommentInput
                        placeholder="댓글을 입력하세요..."
                        value={newComment}
                        onChange={handleNewCommentChange}
                        rows={4}
                    />
                    <SubmitButton type="submit">댓글 작성</SubmitButton>
                </CommentForm>
                <CommentContainer>
                    <h2>댓글</h2>
                    {comments.length === 0 ? (
                        <Comment>
                            <CommentInfo>
                                <CommentContent>댓글이 없습니다.</CommentContent>
                            </CommentInfo>
                        </Comment>
                    ) : null}
                    {comments.map(comment => (
                        <Comment key={comment.commentId}>
                            <CommentInfo>
                                <CommentAuthor>{comment.userNickName}</CommentAuthor>
                                {editMode.active && editMode.id === comment.commentId ? (
                                    <CommentInput
                                        value={editMode.content}
                                        onChange={handleEditChange}
                                        rows={2}
                                    />
                                ) : (
                                    <CommentContent>{comment.content}</CommentContent>
                                )}
                            </CommentInfo>
                            <CommentActions>
                                {editMode.active && editMode.id === comment.commentId ? (
                                    <SubmitButton onClick={handleEditSubmit}>수정 완료</SubmitButton>
                                ) : (
                                    <ActionButton onClick={() => handleEditClick(comment.commentId, comment.content)}>수정</ActionButton>
                                )}
                                <ActionButton onClick={() => handleDelete(comment.commentId)}>삭제</ActionButton>
                            </CommentActions>
                        </Comment>
                    ))}
                </CommentContainer>
            </Container>
        </div>
    );
};

export default PostDetail;
