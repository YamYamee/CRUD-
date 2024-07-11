import React, { useEffect, useState } from 'react';
import NavBar from './NavBar';
import { FaEdit, FaTrashAlt } from 'react-icons/fa';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import axios from "axios";

const Container = styled.div`
    width: 100%;
    padding: 20px;
`;

const Title = styled.h1`
    font-size: 1.5rem;
`;

const AddPostButton = styled.button`
    background-color: #3f51b5;
    color: white;
    border: none;
    padding: 10px 20px;
    cursor: pointer;
    margin-bottom: 20px;

    &:hover {
        background-color: #303f9f;
    }
`;

const Table = styled.table`
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
`;

const TableHead = styled.thead`
    background-color: #f5f5f5;
`;

const TableRow = styled.tr`
    border-bottom: 1px solid #ddd;
    cursor: pointer;

    &:hover {
        background-color: #f0f0f0;
    }
`;

const TableCell = styled.td`
    padding: 10px;
    text-align: left;
`;

const Icon = styled.span`
    cursor: pointer;
    margin-left: 10px;
`;

const Pagination = styled.div`
    display: flex;
    justify-content: center;
    margin-top: 20px;
`;

const PageButton = styled.button`
    background-color: #3f51b5;
    color: white;
    border: none;
    padding: 10px 15px;
    margin: 0 5px;
    cursor: pointer;

    &:hover {
        background-color: #303f9f;
    }

    &:disabled {
        background-color: #b0b0b0;
        cursor: not-allowed;
    }
`;

const ErrorMessage = styled.div`
  color: red;
  font-weight: bold;
  margin-top: 20px;
  text-align: center;
`;

const Posts = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const [pageData, setPageData] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const navigate = useNavigate();
    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const token = sessionStorage.getItem("accessToken");

    const fetchData = async (currentPage) => {
        try {
            const response = await axios.get(`http://15.165.102.113:8080/api/users/posts/home/${currentPage}`);
            setPageData(response.data.result);
            setTotalPages((((response.data.result)[0].totalPost) / 5 | 0) + 1);
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData(currentPage);
    }, []);

    useEffect(() => {
        if (error) {
            const timer = setTimeout(() => {
                window.location.href = 'http://15.165.102.113';
            }, 3000);
            return () => clearTimeout(timer);
        }
    }, [error]);

    if (loading) return <p>Loading...</p>;
    if (error) return <ErrorMessage>오류가 발생했거나 없는 페이지입니다. 3초 후에 홈으로 리디렉션됩니다.</ErrorMessage>;

    const handlePageChange = (page) => {
        setCurrentPage(page);
        fetchData(page);
    };

    const handlePostClick = (postId) => {
        navigate(`/post/${postId}`);
    };

    const handlePostDelete = async (event, postId) => {
        event.stopPropagation();

        try {
            const response = await fetch(`http://15.165.102.113:8080/api/users/posts/${postId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                alert('삭제 성공');
                fetchData(currentPage); // 데이터 새로고침
            } else {
                alert('니 글 아닌데?');
            }
        } catch (error) {
            alert('오류가 발생했습니다.');
        }
    };

    return (
        <div>
            <NavBar />
            <Container>
                <Title>자유 게시판</Title>
                <AddPostButton onClick={() => { navigate('/WritePost') }}>+ Add Post</AddPostButton>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>no.</TableCell>
                            <TableCell>제목</TableCell>
                            <TableCell>작성자</TableCell>
                            <TableCell>작성일</TableCell>
                            <TableCell>수정</TableCell>
                        </TableRow>
                    </TableHead>
                    <tbody>
                    {pageData.map(post => (
                        <TableRow key={post.postId} onClick={() => handlePostClick(post.postId)}>
                            <TableCell>{post.postId}</TableCell>
                            <TableCell>{post.postTitle}</TableCell>
                            <TableCell>{post.postWriter}</TableCell>
                            <TableCell>{post.createdAt}</TableCell>
                            <TableCell>
                                <Icon onClick={(event) => handlePostDelete(event, post.postId)}><FaTrashAlt/></Icon>
                            </TableCell>
                        </TableRow>
                    ))}
                    </tbody>
                </Table>
                <Pagination>
                    <PageButton
                        disabled={currentPage === 1}
                        onClick={() => handlePageChange(currentPage - 1)}
                    >
                        Previous
                    </PageButton>
                    {[...Array(totalPages)].map((_, index) => (
                        <PageButton
                            key={index + 1}
                            disabled={currentPage === index + 1}
                            onClick={() => handlePageChange(index + 1)}
                        >
                            {index + 1}
                        </PageButton>
                    ))}
                    <PageButton
                        disabled={currentPage === totalPages}
                        onClick={() => handlePageChange(currentPage + 1)}
                    >
                        Next
                    </PageButton>
                </Pagination>
            </Container>
        </div>
    );
};

export default Posts;
