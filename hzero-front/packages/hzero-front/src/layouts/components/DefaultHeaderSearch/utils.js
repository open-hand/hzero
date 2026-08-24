import { getSession, setSession } from 'utils/utils';

const menuHistorySessionKey = 'menuHistoryKey';

function storeHistory(history = []) {
  setSession(menuHistorySessionKey, history);
}

function loadHistory() {
  return getSession(menuHistorySessionKey) || [];
}

export { menuHistorySessionKey, storeHistory, loadHistory };
