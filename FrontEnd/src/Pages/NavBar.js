import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';

function NavBar() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const navigate = useNavigate();

    const checkLoginStatus = () => {
        const token = sessionStorage.getItem('accessToken');
        return token ? true : false;
    };

    useEffect(() => {
        // 컴포넌트가 마운트될 때 로그인 상태 확인
        const loggedIn = checkLoginStatus();
        setIsLoggedIn(loggedIn);
    }, []); // 빈 배열이므로 컴포넌트가 마운트될 때 한 번만 실행

    const StyledNavbar = styled.nav`
        background-color: #3f51b5;
        color: white;
        padding: 10px 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    `;

    const NavItem = styled.div`
        display:flex;
        margin: 0 10px;
        cursor: pointer;
    `;

    return (
        <StyledNavbar>
            <NavItem onClick={() => navigate('/')}>🏠 홈</NavItem>
            <NavItem>
                {isLoggedIn ? null : (
                    <NavItem onClick={() => navigate('/login')}>로그인</NavItem>
                )}
                <NavItem>게시판</NavItem>
            </NavItem>
        </StyledNavbar>
    );
}

export default NavBar;
