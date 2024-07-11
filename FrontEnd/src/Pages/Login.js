import React, { useState } from 'react';
import styled from 'styled-components';
import NavBar from "./NavBar";
import {useNavigate} from "react-router-dom";

const Container = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f5f5f5;
`;

const LoginForm = styled.form`
    width: 400px;
    padding: 20px;
    background: white;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    text-align: center;
`;

const Title = styled.h2`
    margin-bottom: 20px;
`;

const Input = styled.input`
    width: 100%;
    padding: 10px;
    margin: 10px 0;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
`;

const Button = styled.button`
    width: 100%;
    padding: 10px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
    margin-bottom: 10px;

    &:hover {
        background-color: #0056b3;
    }
`;

const Link = styled.a`
    display: block;
    margin: 10px 0;
    color: #007bff;
    cursor: pointer;
    text-decoration: none;

    &:hover {
        text-decoration: underline;
    }
`;

const SocialLoginButton = styled.a`
    width: 100%;
    padding: 10px;
    margin: 5px 0;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;

    ${({ color }) => `
    background-color: ${color};
    color: white;
  `}

    &:hover {
        opacity: 0.9;
    }
`;

const Divider = styled.div`
    display: flex;
    align-items: center;
    margin: 20px 0;

    &:before,
    &:after {
        content: '';
        flex: 1;
        border-bottom: 1px solid #ccc;
    }

    &:before {
        margin-right: 10px;
    }

    &:after {
        margin-left: 10px;
    }
`;

const Login = () => {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const CLIENT_ID = process.env.REACT_APP_REST_API_KEY;
    const REDIRECT_URI = process.env.REACT_APP_REDIRECT_URL;
    const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code`;

    const handleSubmit = async (e) => {
        e.preventDefault(); // 기본 폼 제출 동작 방지

        const userData = {
            email,
            password,
        };

        try {
            const response = await fetch('http://15.165.102.113:8080/api/users/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData), // userData post 요청하기
            });


            if (response.ok) {
                const data = await response.json();
                alert('로그인이 성공적으로 완료되었습니다.');
                sessionStorage.setItem('accessToken', data.result.tokenInfo.accessToken);
                Navigate("/");
            } else {
                console.log('로그인 실패:', response);
                alert('아이디 또는 비밀번호를 잘못 입력하셨습니다');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('로그인 중 오류가 발생했습니다.');
        }
    };

    let Navigate = useNavigate();

    return (
        <div>
            <NavBar></NavBar>
            <Container>
                <LoginForm onSubmit={handleSubmit}>
                    <Title>로그인</Title>
                    <Input
                        type="email"
                        placeholder="이메일"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                    <Input
                        type="password"
                        placeholder="비밀번호"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    <Button type="submit">로그인</Button>
                    <Divider>또는</Divider>
                    <SocialLoginButton type="button" color="#03C75A" href = {KAKAO_AUTH_URL}>카카오로 시작하기</SocialLoginButton>
                    <Divider>또는</Divider>
                    <Link onClick={()=>{Navigate('/SignUp')}}>회원가입</Link>
                </LoginForm>
            </Container>
        </div>

    );
};

export default Login;
