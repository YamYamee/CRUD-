import React, { useState } from 'react';
import styled from 'styled-components';
import NavBar from "./NavBar";
import {useNavigate} from "react-router-dom";

const Container = styled.div`
    width: 100%;
    padding: 20px;
`;

const Title = styled.h1`
  font-size: 1.5rem;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
`;

const FormGroup = styled.div`
  margin-bottom: 15px;
    display: flex;
    flex-direction: column;
`;

const Label = styled.label`
  margin-bottom: 5px;
  font-weight: bold;
`;

const Input = styled.input`
    padding: 10px;
    font-size: 1rem;
    border: none;
    border-bottom: 2px solid #3f51b5; /* Specific color for bottom border */
    outline: none;
`;

const TextArea = styled.textarea`
    padding: 10px;
    font-size: 1rem;
    border: none;
    border-bottom: 2px solid #3f51b5; /* 특정 색상으로 하단 경계선 */
    outline: none;
    height: 300px; /* 높이를 300px로 설정 */

    &:focus {
        border-bottom: 2px solid #303f9f; /* 포커스 시 경계선 색상 변경 */
    }
`;

const FileInput = styled.input`
    display: none;
`;

const FileInputLabel = styled.label`
    padding: 10px;
    font-size: 1rem;
    border: 2px solid #3f51b5;
    border-radius: 5px;
    cursor: pointer;
    margin-bottom: 15px;
    text-align: center;

    &:hover {
        background-color: #f1f1f1;
    }
`;

const SubmitButton = styled.button`
  background-color: #3f51b5;
  color: white;
  border: none;
  padding: 10px 20px;
  cursor: pointer;

  &:hover {
    background-color: #303f9f;
  }
`;

const ImageContainer = styled.div`
  position: relative;
  display: inline-block;
`;

const PreviewImage = styled.img`
  max-width: 100%;
  max-height: 300px;
`;

const DeleteButton = styled.button`
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: rgba(255, 255, 255, 0.7);
  border: none;
  color: red;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;

  &:hover {
    background-color: rgba(255, 255, 255, 1);
  }
`;

function WritePosts() {

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [image, setImage] = useState(null);
    const token = sessionStorage.getItem("accessToken");
    const [imagePreview, setImagePreview] = useState('');
    const [imageURL, setImageURL] = useState('');

    let Navigate = useNavigate();

    const handleSubmit = async (e) => {

        e.preventDefault(); // 기본 폼 제출 동작 방지

        const formData = new FormData();
        formData.append('title', title);
        formData.append('content', title);
        if (image) {
            formData.append('PostPicture', image);
        }
        try {
            const response = await fetch('http://15.165.102.113:8080/api/users/posts', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            if (response.ok) {
                const data = await response.json();
                alert('게시물이 성공적으로 작성되었습니다.');
                console.log(data);
                Navigate("/");
            } else {
                console.log('게시물 작성 실패:', response);
                alert('게시물 작성 실패');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('게시물 작성 중 오류가 발생했습니다.');
        }
    };

    const handleFileChange = async (e) => {
        const file = e.target.files[0];
        const name = encodeURIComponent(file);
        setImage(file);

        const reader = new FileReader();
        reader.onloadend = () => {
            setImagePreview(reader.result);
        };
        if (file) {
            reader.readAsDataURL(file);
        }
    };

    const handleImageDelete = () => {
        setImage(null);
        setImagePreview('');
    };


    return (
        <div>
            <NavBar></NavBar>
            <Container>
                <Title>글쓰기</Title>
                <Form>
                    <FormGroup>
                        <Label htmlFor="title">제목</Label>
                        <Input
                            type="title"
                            placeholder="제목"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            required
                        />
                    </FormGroup>
                    <FormGroup>
                        <Label htmlFor="content">내용</Label>
                        <TextArea
                            type="content"
                            placeholder="내용"
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            required></TextArea>

                    </FormGroup>
                </Form>
                <SubmitButton type="submit" onClick={handleSubmit}>등록</SubmitButton>
                <FileInput
                    type="file"
                    id="image"
                    accept="image/*"
                    onChange={handleFileChange}
                />
                <FileInputLabel htmlFor="image">사진 업로드</FileInputLabel>
            </Container>
            {imagePreview && (
                <ImageContainer>
                    <PreviewImage src={imagePreview} alt="Preview" />
                    <DeleteButton onClick={handleImageDelete}>X</DeleteButton>
                </ImageContainer>
            )}
        </div>

    );
}

export default WritePosts;
