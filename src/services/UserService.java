package services;

import models.Member;
import java.util.List;
import repository.UserRepository;

public class UserService {

    /** Returns ONLY team members (frontend + backend). */
    public static List<Member> getTeamMembers() {
        return UserRepository.getAllMembers();
    }
}