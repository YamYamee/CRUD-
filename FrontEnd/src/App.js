import './App.css';
import {Route, Routes} from "react-router-dom";
import Posts from "./Pages/Posts";
import WritePosts from "./Pages/WritePosts";
import Login from "./Pages/Login";
import SignUp from "./Pages/SignUp";
import PostDetail from "./Pages/PostDetail";
import KakaoLogin from "./Pages/KakaoLogin";

function App() {

    return (
        <Routes>
            <Route path="/" element={<Posts/>} />
            <Route path="/auth/callback" element={<KakaoLogin/>} />
            <Route path="/WritePost" element={<WritePosts/>}/>
            <Route path="/Login" element={<Login/>}/>
            <Route path="/SignUp" element={<SignUp/>}/>
            <Route path="/post/:id" element={<PostDetail />} />
            <Route path="*" element={ <div>없는페이지임</div> } />
        </Routes>
    )
}


export default App;
