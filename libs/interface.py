import pickle
from collections import defaultdict
from libs.skgmm import GMMSet
from libs.features import get_feature
import time

class ModelInterface:

    def __init__(self, gmm, feature_type):
        self.features = defaultdict(list)
        self.gmm_order = gmm
        self.feature_type = feature_type

    def enroll(self, name, filename):
        feat = get_feature(filename, self.feature_type)
        self.features[name].extend(feat)

    def train(self):
        self.gmmset = GMMSet(self.gmm_order)
        start_time = time.time()
        for name, feats in self.features.items():
            self.gmmset.fit_new(feats, name)
        return (time.time() - start_time)
    
    def dump(self, fname):
        self.gmmset.before_pickle()
        with open(fname, 'wb') as f:
            pickle.dump(self, f, -1)
        self.gmmset.after_pickle()

    def predict(self, filename):
        try:
            feat = get_feature(filename, self.feature_type)
        except Exception as e:
            print (e)
        return self.gmmset.predict_one(feat)

    @staticmethod
    def load(fname):
        with open(fname, 'rb') as f:
            R = pickle.load(f)
            R.gmmset.after_pickle()
            return R
