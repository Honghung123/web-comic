import SettingServer from '../../components/SettingServer';
import ListComics from '../../components/ListComics';
import ListLastUpdate from '../../components/ListLastUpdated/ListLastUpdated';

export default function Home() {
    return (
        <div className="px-4 py-8">
            <SettingServer />
            <ListComics />
            <ListLastUpdate />
        </div>
    );
}
