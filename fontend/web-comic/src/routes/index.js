// Layouts
// import { HeaderOnlyLayout } from "./components/Layout";

// Import component page
import Home from "./../pages/Home";
import ComicInfo from "./../pages/ComicInfo";
import Reading from "./../pages/Reading";
import AuthorGenreListComics from '../pages/AuthorGenreListComics';

// Public routes
const publicRoutes = [
    { path: "/", component: Home },
    { path: "/info/:serverId/:tagId", component: ComicInfo },
    { path: "/reading/:serverId/:tagId/:chapter", component: Reading },
    { path: "/author/:serverId/:authorId/:tagId", component: AuthorGenreListComics },
    { path: "/genre/:serverId/:genreId", component: AuthorGenreListComics },
];

// Private routes
const privateRoutes = [];

export { publicRoutes, privateRoutes };
