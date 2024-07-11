import React, {useState} from 'react';
import styled from 'styled-components';
import NavBar from "./NavBar";
import {redirect, useNavigate} from "react-router-dom";

const Container = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f5f5f5;
`;

const RegisterForm = styled.form`
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


const SocialRegisterButton = styled.button`
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

function Register () {

    const [nickname, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    let Navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }

        const userData = {
            email,
            password,
            nickname,
        };

        try {
            const response = await fetch('http://15.165.102.113:8080/api/users/sign-up', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData),
            });

            if (response.ok) {
                alert('회원가입이 성공적으로 완료되었습니다.');
                Navigate('/');

            } else {
                alert('회원가입에 실패했습니다.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('회원가입 중 오류가 발생했습니다.');
        }
    };


    return (
        <div>
            <NavBar></NavBar>
            <Container>
                <RegisterForm onSubmit={handleSubmit}>
                    <Title>회원가입</Title>
                    <Input
                        type="text"
                        placeholder="이름"
                        value={nickname}
                        onChange={(e) => setName(e.target.value)}
                    />
                    <Input
                        type="email"
                        placeholder="이메일"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <Input
                        type="password"
                        placeholder="비밀번호"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <Input
                        type="password"
                        placeholder="비밀번호 확인"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                    />
                    <Button type="submit">회원가입</Button>
                    <Divider>또는</Divider>
                    <SocialRegisterButton color="#3b5998">Facebook으로 시작하기</SocialRegisterButton>
                    <SocialRegisterButton color="#db4437">Google로 시작하기</SocialRegisterButton>
                    <SocialRegisterButton color="#03C75A">Naver로 시작하기</SocialRegisterButton>
                </RegisterForm>
            </Container>
        </div>
    );
}

export default Register;
