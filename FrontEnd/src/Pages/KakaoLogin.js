import axios from "axios";
import React, { useEffect } from "react";

function KakaoLogin() {

    // 카카오로 요청보낸 페이지에서 인가코드를 뽑아온다
    const code = new URL(window.location.href).searchParams.get("code");

    // 페이지 랜더링 시, 아래 함수를 실행합니다.
    useEffect(() => {
        (async () => {
            try {
                // 여기서 redirect_uri는 프론트의 redirect_uri입니다.
                // 아래 요청은 카카오 로그인을 성공했을 시의 토큰값을 받기 위함입니다.

                const kakaoResult = await axios.post(
                    `https://kauth.kakao.com/oauth/token?grant_type=authorization_code&client_id=${process.env.REACT_APP_REST_API_KEY}&redirect_uri=${process.env.REACT_APP_REDIRECT_URL}&code=${code}`,
                    {
                        headers: {
                            "Content-type": "application/x-www-form-urlencoded;charset=utf-8",
                            "Access-Control-Allow-Origin": "*"
                        },
                    }
                );

                const token = kakaoResult.data;

                // 저장한 토큰과 data를 백엔드의 redirect_uri로 보내줍니다.
                const response = await fetch(
                    `http://15.165.102.113/api/users/oauth/kakao`, {
                        method : 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body : JSON.stringify(kakaoResult.data)
                    });

                sessionStorage.setItem("accessToken", token.access_token);

                return window.location.replace('/');
            } catch (e) {
                console.error(e);
            }
        })();
    }, []);

    return (
        <div>
        </div>
    );
}

export default KakaoLogin;